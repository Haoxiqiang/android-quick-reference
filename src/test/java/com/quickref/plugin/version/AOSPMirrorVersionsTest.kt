package com.quickref.plugin.version

import org.junit.Test
import java.io.File

class AOSPMirrorVersionsTest {

    @Test
    fun generateVersions() {
        val filePath = "src/test/resources/aosp-mirror-tags.txt"
        val file = File(filePath)
        val baseLength = "android-".length
        val names = file.readLines().filter { tag ->
            tag.startsWith("android-") && tag[baseLength].isDigit()
        }

        val kv = LinkedHashMap<String, String>()
        names
            .filter { name ->
                if (name.length < baseLength + 2 || name.startsWith("android-").not()) {
                    false
                } else {
                    val vs = name.subSequence(baseLength, baseLength + 1)[0]
                    vs.isDigit()
                }
            }
            .sortedWith(VersionComparator())
            .reversed()
            .forEach { version ->
                val maintain = version.substring(0, version.lastIndexOf("_"))
                if (kv.containsKey(maintain).not()) {
                    kv[maintain] = version
                }
            }

        val outPath = "src/test/resources/aosp-mirror-versions.txt"
        val output = File(outPath)
        if (output.exists().not()) {
            output.createNewFile()
        }
        val text = kv.entries.joinToString(separator = "\n", transform = { entry ->
            """Pair("${entry.key}","${entry.value}"),
            """.trimIndent()

        })
        val code = """private val aospVersions = linkedMapOf($text)
        """.trimIndent()
        output.writeText(code)
    }
}
