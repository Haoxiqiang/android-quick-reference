plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.3.0"
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
}

project.group = "com.quickref.plugin"
project.version = "0.5-beta"

dependencies {
    testImplementation("org.json:json:20211205")

    implementation("org.jetbrains:annotations:22.0.0")
    implementation(kotlin("bom", version = "1.6.10"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")

    implementation("io.ktor:ktor-client-core:1.6.7")
    implementation("io.ktor:ktor-client-cio:1.6.7")
    implementation("io.ktor:ktor-client-logging-jvm:1.6.7")


    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.19.0")
}

repositories {
    mavenCentral()
}

val jvmVersion = JavaVersion.VERSION_1_8

java {
    sourceCompatibility = jvmVersion
    targetCompatibility = jvmVersion
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    sourceCompatibility = jvmVersion.toString()
    targetCompatibility = jvmVersion.toString()
    kotlinOptions {
        jvmTarget = jvmVersion.toString()
        apiVersion = "1.6"
        languageVersion = "1.6"
        allWarningsAsErrors = true
    }
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("203.7717.56")
    type.set("IC")
    plugins.set(listOf("com.intellij.java"))
}

// it's for dev
tasks.withType(org.jetbrains.intellij.tasks.RunIdeTask::class.java) {
    // you should set your AndroidStudio's app dir like this.
    // ideDir.set(file("/Users/haoxiqiang/Library/Application Support/JetBrains/Toolbox/apps/AndroidStudio/ch-0/203.7935034/Contents"))
    autoReloadPlugins.set(true)
}

tasks {
    patchPluginXml {
        version.set("${project.version}")
        sinceBuild.set("200")
        changeNotes.set(
            """
            A android source reference tool.<br>
            <em>
                <li>quick search online</li>
                <li>view/diff/download the all versions from the aosp</li>
            </em>        """.trimIndent()
        )
        pluginDescription.set(
            """
            A android source reference tool.
            """.trimIndent()
        )
    }
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
    jvmTarget = jvmVersion.toString()
    exclude("resources/")
    exclude("build/")
    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
        xml.required.set(false) // checkstyle like format mainly for integrations like Jenkins
        txt.required.set(false) // similar to the console output, contains issue signature to manually edit baseline files
        sarif.required.set(true) // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with Github Code Scanning
    }
}
