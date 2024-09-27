package com.quickref.plugin.version

import org.jsoup.Jsoup
import org.junit.Test
import java.io.File

class AOSPMirrorVersionsTest {

    @Test
    fun generateVersions() {

        val url = "https://android.googlesource.com/platform/frameworks/base.git/+refs"
        val document = Jsoup.connect(url).get()
        val baseLength = "android-".length
        val names = document.select("ul.RefList-items li a")
            .map {
                it.text()
            }.filter {
                it.startsWith("android-") && it[8].isDigit()
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

        val outPath = "src/main/kotlin/com/quickref/plugin/version/AOSPMirrorVersion.kt"
        val output = File(outPath)
        if (output.exists().not()) {
            output.createNewFile()
        }
        val text = kv.entries.joinToString(separator = "\n\t\t", transform = { entry ->
            """Pair("${entry.key}","${entry.value}"),
            """.trimIndent()

        })
        val code = """package com.quickref.plugin.version

class AOSPMirrorVersion : Version() {

    private val aospVersions = linkedMapOf(
        $text
    )

    override fun versionPairs(): HashMap<String, String> {
        return aospVersions
    }

    override fun isDownloadable(): Boolean {
        return true
    }
}
""".trimIndent()
        output.writeText(code)
    }
}
