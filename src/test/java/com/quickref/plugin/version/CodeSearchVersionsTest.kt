package com.quickref.plugin.version

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import java.io.File

class CodeSearchVersionsTest {

    @Test
    fun generateVersions() {
        val jsonPath = "src/test/resources/android-versions.json"
        val file = File(jsonPath)
        val json = JSONObject(file.readText())
        val rootNode: JSONObject = json.getJSONObject("roots")
        val branchTree: JSONArray = rootNode.getJSONArray("branch")
        val size = branchTree.length()
        val names = mutableListOf<String>()
        for (i in 0 until size) {
            val branch: JSONObject = branchTree.getJSONObject(i)
            names.add(branch.getString("branchName"))
        }
        val baseLength = "android-".length
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

        val outPath = "src/test/resources/android-versions.txt"
        val output = File(outPath)
        if (output.exists().not()) {
            output.createNewFile()
        }
        val text = kv.entries.joinToString(separator = "\n", transform = { entry ->
            """Pair("${entry.key}","${entry.value}"),
            """.trimIndent()

        })
        val code = """private val codeSearchVersions = linkedMapOf($text)
        """.trimIndent()
        output.writeText(code)
    }
}
