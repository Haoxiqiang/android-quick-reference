import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.14.2"
    id("org.jetbrains.changelog") version "2.2.1"
    id("org.jetbrains.kotlin.jvm") version "1.8.22"
    id("io.gitlab.arturbosch.detekt") version "1.23.7"
    id("com.diffplug.spotless")
    id("app.cash.sqldelight")
}

group = properties("pluginGroup")
version = properties("pluginVersion")

dependencies {

    testImplementation("org.json:json:20231013")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jsoup:jsoup:1.15.3")
    // https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
    testImplementation("org.xerial:sqlite-jdbc:3.46.1.3")

    implementation("org.jetbrains:annotations:24.0.1")
    implementation(kotlin("bom", version = "1.8.22"))

    implementation("app.cash.sqldelight:sqlite-driver:2.0.2") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
    }
    implementation("app.cash.sqldelight:runtime-jvm:2.0.2") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
    }

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.7")
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
    // AI - Android Studio
    // IC - IntelliJ IDEA Community Edition
    // IU - IntelliJ IDEA Ultimate Edition
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
    ideDir.set(file("/Applications/Android Studio.app/Contents"))
    autoReloadPlugins.set(true)
}

tasks.withType(org.jetbrains.intellij.tasks.BuildSearchableOptionsTask::class.java) {
    enabled = true
}

tasks {
    properties("javaVersion").let { version ->
        withType<JavaCompile> {
            sourceCompatibility = version
            targetCompatibility = version
        }
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = version
            kotlinOptions.apiVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_8.version
            kotlinOptions.languageVersion = "1.8"
            kotlinOptions.allWarningsAsErrors = false
        }
    }

    wrapper {
        gradleVersion = properties("gradleVersion")
    }

    patchPluginXml {

        sinceBuild.set("223")

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
            changelog.renderItem(changelog.run {
                getOrNull(properties("pluginVersion")) ?: getLatest()
            }, Changelog.OutputType.HTML)
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
    databases {
        create("QuickRefDB") {
            packageName.set("com.quickref.plugin.db")
            schemaOutputDirectory.set(file("src/main/sqldelight/databases"))
        }
    }
    linkSqlite.set(true)
}

// code analysis
detekt {
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
    @Suppress("DEPRECATION")
    config =
        files("$projectDir/config/detekt.yml") // point to your custom config defining rules to run, overwriting default behavior
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
