package com.quickref.plugin

import com.quickref.plugin.version.VersionComparator
import org.jetbrains.kotlin.util.collectionUtils.filterIsInstanceAnd
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import java.io.File

class SourceGraphVersionsTest {

    @Test
    fun generateVersions() {
        val jsonPath = "src/test/resources/sourcegraph.json"
        val file = File(jsonPath)
        val json = JSONObject(file.readText())
        val nodes: JSONArray =
            json.getJSONObject("data")
                .getJSONObject("node")
                .getJSONObject("gitRefs")
                .getJSONArray("nodes")!!

        val size = nodes.length()
        val names = mutableListOf<String>()
        for (i in 0 until size) {
            val branch: JSONObject = nodes.getJSONObject(i)
            names.add(branch.getString("displayName"))
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
            .filterIsInstanceAnd<String> { version ->
                val maintain = version.substring(0, version.lastIndexOf("_"))
                if (kv.containsKey(maintain)) {
                    false
                } else {
                    kv[maintain] = version
                    true
                }
            }

        val outPath = "src/test/resources/source-graph-versions.txt"
        val output = File(outPath)
        if (output.exists().not()) {
            output.createNewFile()
        }

        val text = kv.entries.joinToString(separator = "\n", transform = { entry ->
            """Pair("${entry.key}","${entry.value}"),
            """.trimIndent()

        })
        val code = """private val sourceGraphVersions = linkedMapOf($text)
        """.trimIndent()
        output.writeText(code)
    }
}
