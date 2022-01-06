plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.3.0"
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
}

group = "com.quickref.plugin"
version = "0.5-beta"

dependencies {
    implementation("org.jetbrains:annotations:22.0.0")
    implementation(kotlin("bom", version = "1.6.10"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")

    implementation("io.ktor:ktor-client-core:1.6.7")
    implementation("io.ktor:ktor-client-cio:1.6.7")
    implementation("io.ktor:ktor-client-logging-jvm:1.6.7")
}

repositories {
    mavenCentral()
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java) {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
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
        untilBuild.set("213.*")
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
