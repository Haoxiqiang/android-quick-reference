import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.3.0"
    id("org.jetbrains.changelog") version "1.3.1"
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
    id("com.diffplug.spotless")
    id("com.squareup.sqldelight")
}

group = properties("pluginGroup")
version = properties("pluginVersion")

dependencies {

    testImplementation("org.json:json:20211205")
    testImplementation("junit:junit:4.13.2")
    // git repo.
    testImplementation("org.eclipse.jgit:org.eclipse.jgit:6.0.0.202111291000-r")

    implementation("org.jetbrains:annotations:23.0.0")
    implementation(kotlin("bom", version = "1.6.10"))

    implementation("com.squareup.sqldelight:sqlite-driver:1.5.3")
    implementation("com.squareup.sqldelight:runtime:1.5.3")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.19.0")
}

repositories {
    mavenCentral()
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    updateSinceUntilBuild.set(false)
    sameSinceUntilBuild.set(true)
    type.set(properties("platformType"))

    plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

changelog {
    version.set(properties("pluginVersion"))
    groups.set(emptyList())
}

// it's for dev
tasks.withType(org.jetbrains.intellij.tasks.RunIdeTask::class.java) {
    // you should set your AndroidStudio's app dir like this.
    // ideDir.set(file("/Users/haoxiqiang/Library/Application Support/JetBrains/Toolbox/apps/AndroidStudio/ch-0/203.7935034/Android Studio.app/Contents"))
    autoReloadPlugins.set(true)
}

tasks {
    properties("javaVersion").let {
        withType<JavaCompile> {
            sourceCompatibility = it
            targetCompatibility = it
        }
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = it
            kotlinOptions.apiVersion = "1.6"
            kotlinOptions.languageVersion = "1.6"
            kotlinOptions.allWarningsAsErrors = false
        }
    }

    wrapper {
        gradleVersion = properties("gradleVersion")
    }

    patchPluginXml {
        sinceBuild.set("193")

        version.set(properties("pluginVersion"))
        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription.set(
            projectDir.resolve("README.md").readText().lines().run {
                val start = "<!-- Plugin description -->"
                val end = "<!-- Plugin description end -->"

                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end))
            }.joinToString("\n").run { markdownToHTML(this) }
        )

        // Get the latest available change notes from the changelog file
        changeNotes.set(provider {
            changelog.run {
                getOrNull(properties("pluginVersion")) ?: getLatest()
            }.toHTML()
        })
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token.set(System.getenv("PUBLISH_TOKEN"))
        // pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels.set(listOf(properties("pluginVersion").split('-').getOrElse(1) { "default" }.split('.').first()))
    }
}

// database support
sqldelight {
    database("QuickRefDB") {
        packageName = "com.quickref.plugin.db"
        schemaOutputDirectory = file("src/main/sqldelight/databases")
        dialect = "sqlite:3.25"
    }
    linkSqlite = true
}

// code analysis
detekt {
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
    config =
        files("$projectDir/config/detekt.yml") // point to your custom config defining rules to run, overwriting default behavior
    baseline = file("$projectDir/config/baseline.xml") // a way of suppressing issues before introducing detekt
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    exclude("resources/")
    exclude("build/")
    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
        xml.required.set(false) // checkstyle like format mainly for integrations like Jenkins
        txt.required.set(false) // similar to the console output, contains issue signature to manually edit baseline files
        sarif.required.set(true) // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with Github Code Scanning
    }
}

// code formatter
val excludeDirs = listOf("build/", "src/test/resources/")
extensions.findByType<com.diffplug.gradle.spotless.SpotlessExtension>()?.apply {
    format("misc") {
        target("*.gradle", "*.md", ".gitignore")
        indentWithSpaces(4)
        trimTrailingWhitespace()
        endWithNewline()
    }
    java {
        target("**/*.java")
        excludeDirs.forEach { dir ->
            targetExclude(dir)
        }
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }

    kotlin {
        target("**/*.kt")
        excludeDirs.forEach { dir ->
            targetExclude(dir)
        }
        trimTrailingWhitespace()
        endWithNewline()
    }

    kotlinGradle {
        target("*.gradle.kts")
        excludeDirs.forEach { dir ->
            targetExclude(dir)
        }
    }
}

tasks.register("createPreCommitHook") {
    val gitHooksDirectory = File(project.rootDir, ".git/hooks/")
    if (!gitHooksDirectory.exists()) {
        gitHooksDirectory.mkdirs()
    }
    val source = File(project.rootDir, "config/pre-commit")
    val target = File(gitHooksDirectory, "pre-commit")
    source.copyTo(target, true)

    if (!target.canExecute()) {
        println("make pre-commit execute")
        target.setExecutable(true)
    }
}
