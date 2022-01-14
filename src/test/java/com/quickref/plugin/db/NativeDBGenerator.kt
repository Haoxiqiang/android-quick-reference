package com.quickref.plugin.db

import com.quickref.plugin.version.AndroidVersion
import com.quickref.plugin.version.Source
import org.junit.Before
import org.junit.Test
import java.io.File

class NativeDBGenerator {

    @Before
    @Suppress("MaxLineLength")
    fun setup() {
        val nativeFileSQL = File("src/main/sqldelight/com/quickref/plugin/db/NativeFileMapping.sq")
        val nativeMethodSQL = File("src/main/sqldelight/com/quickref/plugin/db/NativeMethodMapping.sq")
        val nativeFileTable = """
        CREATE TABLE NativeFileMapping (
          psiFile TEXT NOT NULL,
          version INTEGER DEFAULT 0,
          path TEXT NOT NULL
        );

        CREATE INDEX NativeFileMapping_path ON NativeFileMapping(psiFile,version);

        selectAll:
        SELECT psiFile,version,path FROM NativeFileMapping;

        getNativeFile:
        SELECT path FROM NativeFileMapping WHERE psiFile=:file AND version <=:version ORDER BY version DESC LIMIT 1;

        insertNativeFile:
        INSERT INTO NativeFileMapping(psiFile,version,path) VALUES (?, ?,?);

    """.trimIndent()

        val nativeMethodTable = """
        CREATE TABLE NativeMethodMapping (
          file TEXT NOT NULL,
          jniMethod TEXT NOT NULL,
          nativeMethod TEXT NOT NULL,
          version INTEGER DEFAULT 0,
          jniLine INTEGER DEFAULT 0,
          defLine INTEGER DEFAULT 0
        );

        CREATE INDEX NativeMapping_line ON NativeMethodMapping(file,jniMethod,nativeMethod,version,jniLine,defLine);

        getNativeMethod:
        SELECT * FROM NativeMethodMapping WHERE file=:key AND jniMethod=:method AND version <=:version ORDER BY version DESC LIMIT 1;

    """.trimIndent()

        nativeFileSQL.writeText(nativeFileTable)
        nativeMethodSQL.writeText(nativeMethodTable)
    }

    @Test
    fun aospDirTest() {
        AndroidVersion
            .getVersionSource(Source.GithubAOSP)
            .versionPairs()
            .entries
            .forEach { entry ->
                val branch = entry.value
                val source = File("src/test/resources/cpp_files/$branch")
                val versionNumber = AndroidVersion.getBuildNumber(branch)
                println("${source.exists()} $source branch:$branch version:$versionNumber")
                assert(source.exists())
                assert(versionNumber != 0)
            }
    }

    @Test
    fun generatorDisSQL() {
        val disSQL = File("src/test/resources/cpp_files/NativeDis.sql")
        @Suppress("MaxLineLength")
        disSQL.writeText(
            """DELETE FROM NativeFileMapping AS a
WHERE (a.psiFile,a.version,a.path) IN (SELECT psiFile,version,path FROM NativeFileMapping GROUP BY psiFile,version,path HAVING count(*) > 1)
AND rowid NOT IN (SELECT min(rowid) FROM NativeFileMapping GROUP BY psiFile,version,path HAVING count(*)>1);
DELETE FROM NativeMethodMapping AS a
WHERE (a.file,a.jniMethod,a.nativeMethod,a.version,a.jniLine,a.defLine) IN (SELECT file,jniMethod,nativeMethod,version,jniLine,defLine FROM NativeMethodMapping GROUP BY file,jniMethod,nativeMethod,version,jniLine,defLine HAVING count(*) > 1)
AND rowid NOT IN (SELECT min(rowid) FROM NativeMethodMapping GROUP BY file,jniMethod,nativeMethod,version,jniLine,defLine HAVING count(*)>1);
        """.trimIndent()
        )
    }

    @Test
    fun generator() {
        val fileSQL = File("src/test/resources/cpp_files/NativeFileMapping.sql")
        val methodSQL = File("src/test/resources/cpp_files/NativeMethodMapping.sql")
        fileSQL.writeText("")
        methodSQL.writeText("")

        AndroidVersion
            .getVersionSource(Source.GithubAOSP)
            .versionPairs()
            .entries
            .forEach { entry ->
                val branch = entry.value
                val source = File("src/test/resources/cpp_files/$branch")
                val versionNumber = AndroidVersion.getBuildNumber(branch)
                source.walk()
                    .maxDepth(depth = 10)
                    .filter { file -> file.isDirectory.not() }
                    .filter { file -> file.readLines().any { line -> line.contains("JNINativeMethod") } }
                    .forEach { file ->
                        // native key
                        val nativeFileBuilder = StringBuilder()
                        val nativeMethodBuilder = StringBuilder()
                        readNativeFileLines(file, source, versionNumber, nativeFileBuilder, nativeMethodBuilder)
                        if (nativeFileBuilder.isNotEmpty()) {
                            fileSQL.appendText(nativeFileBuilder.toString())
                        }
                        if (nativeMethodBuilder.isNotEmpty()) {
                            methodSQL.appendText(nativeMethodBuilder.toString())
                        }
                    }

            }

    }

    private fun readNativeFileLines(
        file: File,
        source: File,
        versionNumber: Int,
        nativeFileBuilder: StringBuilder,
        nativeMethodBuilder: StringBuilder
    ) {
        val packageIndex = file.name.lastIndexOf("_")
        val nativeKey = if (packageIndex >= 0) {
            file.name.substring(packageIndex + 1)
        } else {
            file.name
        }
        val nativePath = file.parent.replace(source.path, "") + "/" + file.name
        val nativeVersion: Int = versionNumber

        println("key:$nativeKey version:$nativeVersion path:$nativePath ")
        // try to record native file
        //INSERT INTO NativeFileMapping (psiFile,version,path)
        //VALUES ("android/graphics/Bitmap.cpp",29,"core/jni/android/graphics/Bitmap.cpp");
        nativeFileBuilder.append(
            "INSERT INTO NativeFileMapping (psiFile,version,path) \n"
                + "VALUES (\"$nativeKey\",$nativeVersion,\"$nativePath\");\n"
        )

        // read jni content.
        val nativeDefineContent = readNativeMethods(file)
        val methods = nativeDefineContent.split("},")

        methods.forEach { methodLine ->
            val list = methodLine.split(",")
            if (list.size != 3) {
                // println("error:${file.absolutePath}\n$nativeDefineContent")
                return@forEach
            }
            val param1 = list[0]
            val param2 = list[1]
            val param3 = list[2] //  (void*)android_drm_DrmManagerClient_closeConvertSession

            val jniMethod = param1.replace("\"", "").replace(" ", "")
            //val jniMethodDescriptor = param2

            val nativeMethod = param3.replace(" ", "").replace("(void*)", "")

            var nativeMethodLine = -1
            var jniLine = -1

            file.readLines().forEachIndexed { index, text ->
                if (text.contains(jniMethod)) {
                    jniLine = index
                }
                val maybeRealDef1 = " $nativeMethod("
                val maybeRealDef2 = " $nativeMethod ("
                if ((text.contains(maybeRealDef1) || text.contains(maybeRealDef2)) && !text.contains(param3)) {
                    nativeMethodLine = index
                }
                if (jniLine > 0 && nativeMethodLine > 0) {
                    return@forEachIndexed
                }
            }

            println(
                "jni:$jniMethod descriptor:$param2 " +
                    "jni-line:$jniLine nativeMethod:$nativeMethod nativeLine:$nativeMethodLine"
            )

            nativeMethodBuilder.append(
                " INSERT INTO NativeMethodMapping " +
                    "(file,jniMethod,nativeMethod,version,jniLine,defLine) \n"
                    + "VALUES (" +
                    "\"$nativeKey\",\"$jniMethod\",\"$nativeMethod\",$nativeVersion,$jniLine,$nativeMethodLine" +
                    ");\n"
            )
        }
    }

    private fun readNativeMethods(
        file: File
    ): String {
        var beginIndex = Int.MAX_VALUE
        var content = 0
        val contentBuilder = StringBuilder()
        file.readLines()
            .filter { line -> line.isNotEmpty() }
            .mapIndexed { index, line ->
                line.trim().apply {
                    if (contains("JNINativeMethod")) {
                        beginIndex = index
                        content = 1
                    }
                }
            }
            .forEachIndexed { index, line ->
                // try to find method line
                if (index > beginIndex) {
                    // { "ctor", "(Ljava/io/FileDescriptor;)I", (void*)ctor_native },
                    if (line.contains("{")) {
                        content += 1
                    }
                    // read content.
                    if (content > 1) {
                        contentBuilder.append(line)
                    }

                    if (line.contains("}")) {
                        content -= 1
                        if (content < 1) {
                            beginIndex = Int.MAX_VALUE
                            return@forEachIndexed
                        }
                    }
                }
            }
        return contentBuilder.toString()
            .trim()
            .replace("{", "")
            .replace("\n", "")
    }
}
