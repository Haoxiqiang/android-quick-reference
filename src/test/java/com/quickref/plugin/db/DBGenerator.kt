@file:Suppress("MaxLineLength")

package com.quickref.plugin.db

import com.quickref.plugin.git.RepoType
import com.quickref.plugin.git.getAOSPRootPath
import com.quickref.plugin.version.AndroidVersion
import com.quickref.plugin.version.Source
import org.junit.Before
import org.junit.Test
import java.io.File

class DBGenerator {

    private val javaFileSQ = File("src/main/sqldelight/com/quickref/plugin/db/JavaFileMapping.sq")
    private val nativeFileSQ = File("src/main/sqldelight/com/quickref/plugin/db/NativeFileMapping.sq")
    private val nativeMethodSQ = File("src/main/sqldelight/com/quickref/plugin/db/NativeMethodMapping.sq")

    @Before
    fun setup() {

        val javaFileTable = """
        CREATE TABLE JavaFileMapping (
          psiFile TEXT NOT NULL,
          version INTEGER DEFAULT 0,
          path TEXT NOT NULL
        );

        CREATE INDEX JavaFileMapping_path ON JavaFileMapping(psiFile,version);

        selectAll:
        SELECT psiFile,version,path FROM JavaFileMapping;

        count:
        SELECT count(*) FROM JavaFileMapping;

        getJavaFile:
        SELECT path FROM JavaFileMapping WHERE psiFile=:file AND version >=:version ORDER BY version ASC LIMIT 1;

    """.trimIndent()

        val nativeFileTable = """
        CREATE TABLE NativeFileMapping (
          psiFile TEXT NOT NULL,
          version INTEGER DEFAULT 0,
          path TEXT NOT NULL
        );

        CREATE INDEX NativeFileMapping_path ON NativeFileMapping(psiFile,version);

        selectAll:
        SELECT psiFile,version,path FROM NativeFileMapping;

        count:
        SELECT count(*) FROM NativeFileMapping;

        getNativeFile:
        SELECT path FROM NativeFileMapping WHERE psiFile=:file AND version >=:version ORDER BY version ASC LIMIT 1;
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
        SELECT * FROM NativeMethodMapping WHERE file=:key AND jniMethod=:method AND version >=:version ORDER BY version ASC LIMIT 1;

        count:
        SELECT count(*) FROM NativeMethodMapping;

        getNativeMethodByName:
        SELECT * FROM NativeMethodMapping WHERE jniMethod=:name LIMIT 1;
    """.trimIndent()

        javaFileSQ.writeText(javaFileTable)
        nativeFileSQ.writeText(nativeFileTable)
        nativeMethodSQ.writeText(nativeMethodTable)
    }

    @Test
    fun aospDirTest() {
        AndroidVersion
            .getVersionSource(Source.AOSPMirror)
            .versionPairs()
            .entries
            .forEach { entry ->
                val branch = entry.value
                val javaSource = File("${getAOSPRootPath(RepoType.AOSP_BASE)}/aosp_base/java_files/$branch")
                val cppSource = File("${getAOSPRootPath(RepoType.AOSP_BASE)}/aosp_base/cpp_files/$branch")

                val versionNumber = AndroidVersion.getBuildNumber(branch)
                println("branch:$branch version:$versionNumber")

                assert(versionNumber != 0)
                assert(javaSource.exists())
                assert(cppSource.exists())
            }
    }

    @Test
    fun generatorDisSQL() {
        val javaSQL = """
        DELETE FROM JavaFileMapping AS a
        WHERE(
            a.psiFile,
            a.version,
            a.path
        ) IN (SELECT psiFile, version, path FROM JavaFileMapping GROUP BY psiFile, version, path HAVING count(*) > 1)
        AND rowid NOT IN (SELECT min (rowid) FROM JavaFileMapping GROUP BY psiFile, version, path HAVING count(*)>1);
        """.trimIndent()

        val cppSQL = """
        DELETE FROM NativeFileMapping AS a
            WHERE(
                a.psiFile,
                a.version,
                a.path
            ) IN (SELECT psiFile, version, path FROM NativeFileMapping GROUP BY psiFile, version, path HAVING count(*) > 1)
        AND rowid NOT IN (SELECT min (rowid) FROM NativeFileMapping GROUP BY psiFile, version, path HAVING count(*)>1);
        DELETE FROM NativeMethodMapping AS a
        WHERE(
            a.file,
            a.jniMethod,
            a.nativeMethod,
            a.version,
            a.jniLine,
            a.defLine
        ) IN (SELECT file, jniMethod, nativeMethod, version, jniLine, defLine FROM NativeMethodMapping GROUP BY file, jniMethod, nativeMethod, version, jniLine, defLine HAVING count(*) > 1)
        AND rowid NOT IN (SELECT min (rowid) FROM NativeMethodMapping GROUP BY file, jniMethod, nativeMethod, version, jniLine, defLine HAVING count(*)>1);
        """.trimIndent()

        val javaDisSQL = File("src/test/resources/JavaDis.sql")
        val cppDisSQL = File("src/test/resources/NativeDis.sql")
        javaDisSQL.writeText(javaSQL)
        cppDisSQL.writeText(cppSQL)
    }

    @Test
    fun generator() {

        val javaSourceSQL = File("src/test/resources/JavaSource.sql")
        javaSourceSQL.writeText("")
        AndroidVersion
            .getVersionSource(Source.AOSPMirror)
            .versionPairs()
            .entries
            .forEach { entry ->
                val branch = entry.value
                val javaSource = File("${getAOSPRootPath(RepoType.AOSP_BASE)}/aosp_base/java_files/$branch")
                val versionNumber = AndroidVersion.getBuildNumber(branch)
                javaSource.walk()
                    .maxDepth(depth = 16)
                    .filter { file -> file.isDirectory.not() }
                    .forEach { file ->
                        // java key
                        val javaFileBuilder = StringBuilder()
                        readJavaFileLines(file, javaSource, versionNumber, javaFileBuilder)
                        if (javaFileBuilder.isNotEmpty()) {
                            javaSourceSQL.appendText(javaFileBuilder.toString())
                        }
                    }
            }


        val cppSourceSQL = File("src/test/resources/CppSource.sql")
        val cppMethodSQL = File("src/test/resources/CppMethod.sql")
        cppSourceSQL.writeText("")
        cppMethodSQL.writeText("")

        AndroidVersion
            .getVersionSource(Source.AOSPMirror)
            .versionPairs()
            .entries
            .forEach { entry ->
                val branch = entry.value
                val cppSource = File("${getAOSPRootPath(RepoType.AOSP_BASE)}/aosp_base/cpp_files/$branch")
                val versionNumber = AndroidVersion.getBuildNumber(branch)
                cppSource.walk()
                    .maxDepth(depth = 10)
                    .filter { file -> file.isDirectory.not() }
                    .filter { file -> file.readLines().any { line -> line.contains("JNINativeMethod") } }
                    .forEach { file ->
                        // native key
                        val nativeFileBuilder = StringBuilder()
                        val nativeMethodBuilder = StringBuilder()
                        readNativeFileLines(file, cppSource, versionNumber, nativeFileBuilder, nativeMethodBuilder)
                        if (nativeFileBuilder.isNotEmpty()) {
                            cppSourceSQL.appendText(nativeFileBuilder.toString())
                        }
                        if (nativeMethodBuilder.isNotEmpty()) {
                            cppMethodSQL.appendText(nativeMethodBuilder.toString())
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
            "INSERT INTO JavaFileMapping (psiFile,version,path) VALUES (\"$javaKey\",$nativeVersion,\"$nativePath\");\n"
        )
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
            "INSERT INTO NativeFileMapping (psiFile,version,path) VALUES (\"$nativeKey\",$nativeVersion,\"$nativePath\");\n"
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
                    "(file,jniMethod,nativeMethod,version,jniLine,defLine) VALUES (" +
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
