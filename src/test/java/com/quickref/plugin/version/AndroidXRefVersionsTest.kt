package com.quickref.plugin.version

import org.junit.Test
import java.io.File

class AndroidXRefVersionsTest {

    @Test
    fun generateVersions() {
        val jsonPath = "src/test/resources/androidxref-versions.txt"
        val file = File(jsonPath)

        val lines = file.readLines()

        val outPath = "src/test/resources/androidxref.txt"
        val output = File(outPath)
        if (output.exists().not()) {
            output.createNewFile()
        }

        val text = lines
            .map {
                val index = it.lastIndexOf(" ")
                it.substring(index + 1)
            }
            .sortedWith(VersionComparator())
            .reversed()
            .joinToString(separator = "\n", transform = { version ->
                // 9.0.0_r3 or 4.4
                val index = version.indexOf("_")
                """Pair("android-${
                    if (index >= 0) {
                        version.substring(0, index)
                    } else {
                        version
                    }
                }","$version"),
            """.trimIndent()
            })
        val code = """private val androidXRefVersions = linkedMapOf($text)
        """.trimIndent()
        output.writeText(code)
    }
}
