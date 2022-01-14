package com.quickref.plugin.db

import com.quickref.plugin.version.AndroidVersion
import com.quickref.plugin.version.Source
import org.junit.Before
import org.junit.Test
import java.io.File

class JavaDBGenerator {

    @Before
    @Suppress("MaxLineLength")
    fun setup() {
        val javaFileSQL = File("src/main/sqldelight/com/quickref/plugin/db/JavaFileMapping.sq")
        val javaFileTable = """
        CREATE TABLE JavaFileMapping (
          psiFile TEXT NOT NULL,
          version INTEGER DEFAULT 0,
          path TEXT NOT NULL
        );

        CREATE INDEX JavaFileMapping_path ON JavaFileMapping(psiFile,version);

        selectAll:
        SELECT psiFile,version,path FROM JavaFileMapping;

        getNativeFile:
        SELECT path FROM JavaFileMapping WHERE psiFile=:file AND version <=:version ORDER BY version DESC LIMIT 1;

        insertNativeFile:
        INSERT INTO JavaFileMapping(psiFile,version,path) VALUES (?, ?,?);

    """.trimIndent()
        javaFileSQL.writeText(javaFileTable)
    }

    @Test
    fun aospDirTest() {
        AndroidVersion
            .getVersionSource(Source.GithubAOSP)
            .versionPairs()
            .entries
            .forEach { entry ->
                val branch = entry.value
                val source = File("src/test/resources/java_files/$branch")
                val versionNumber = AndroidVersion.getBuildNumber(branch)
                println("${source.exists()} $source branch:$branch version:$versionNumber")
                assert(source.exists())
                assert(versionNumber != 0)
            }
    }

    @Test
    fun generatorDisSQL() {

        val disSQL = File("src/test/resources/java_files/JavaDis.sql")
        @Suppress("MaxLineLength")
        disSQL.writeText(
            """DELETE FROM JavaFileMapping AS a
WHERE (a.psiFile,a.version,a.path) IN (SELECT psiFile,version,path FROM JavaFileMapping GROUP BY psiFile,version,path HAVING count(*) > 1)
AND rowid NOT IN (SELECT min(rowid) FROM JavaFileMapping GROUP BY psiFile,version,path HAVING count(*)>1);
""".trimIndent()
        )
    }

    @Test
    fun generator() {

        val fileSQL = File("src/test/resources/java_files/JavaFileMapping.sql")
        fileSQL.writeText("")

        AndroidVersion
            .getVersionSource(Source.GithubAOSP)
            .versionPairs()
            .entries
            .forEach { entry ->
                val branch = entry.value
                val source = File("src/test/resources/java_files/$branch")
                val versionNumber = AndroidVersion.getBuildNumber(branch)
                source.walk()
                    .maxDepth(depth = 16)
                    .filter { file -> file.isDirectory.not() }
                    .forEach { file ->
                        // java key
                        val javaFileBuilder = StringBuilder()
                        readJavaFileLines(file, source, versionNumber, javaFileBuilder)
                        if (javaFileBuilder.isNotEmpty()) {
                            fileSQL.appendText(javaFileBuilder.toString())
                        }
                    }
            }
    }

    private fun readJavaFileLines(
        file: File,
        source: File,
        versionNumber: Int,
        javaFileBuilder: StringBuilder,
    ) {
        val packageIndex = file.name.lastIndexOf("_")
        val javaKey = if (packageIndex >= 0) {
            file.name.substring(packageIndex + 1)
        } else {
            file.name
        }
        val nativePath = file.parent.replace(source.path, "") + "/" + file.name
        val nativeVersion: Int = versionNumber

        println("key:$javaKey version:$nativeVersion path:$nativePath ")
        // try to record native file
        //INSERT INTO JavaFileMapping (psiFile,version,path)
        //VALUES ("android/graphics/Bitmap.cpp",29,"core/jni/android/graphics/Bitmap.cpp");
        javaFileBuilder.append(
            "INSERT INTO JavaFileMapping (psiFile,version,path) \n"
                + "VALUES (\"$javaKey\",$nativeVersion,\"$nativePath\");\n"
        )
    }
}
