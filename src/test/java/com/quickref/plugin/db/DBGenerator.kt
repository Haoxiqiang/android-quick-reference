@file:Suppress("MaxLineLength")

package com.quickref.plugin.db

import com.quickref.plugin.version.AndroidVersion
import com.quickref.plugin.version.Source
import org.junit.Before
import org.junit.Test
import java.io.File
import java.sql.DriverManager
import java.sql.SQLException


class DBGenerator {

    @Test
    fun aospDirTest() {
        AndroidVersion
            .getVersionSource(Source.AOSPMirror)
            .versionPairs()
            .entries
            .forEach { entry ->
                val branch = entry.value

                val javaSource = File("${RepoType.AOSP_BACKUP.getAOSPRootPath()}/$branch/java")
                val cppSource = File("${RepoType.AOSP_BACKUP.getAOSPRootPath()}/$branch/cpp")

                val versionNumber = AndroidVersion.getBuildNumber(branch)
                println("branch:$branch version:$versionNumber")
                println("javaSource:$javaSource")
                println("cppSource:$cppSource")

                assert(versionNumber != 0)
                assert(javaSource.exists())
                assert(cppSource.exists())
            }
    }

    private val javaFileSQ = File("src/main/sqldelight/com/quickref/plugin/db/JavaFileMapping.sq")
    private val nativeFileSQ = File("src/main/sqldelight/com/quickref/plugin/db/NativeFileMapping.sq")

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
        SELECT psiFile,version,path FROM JavaFileMapping WHERE psiFile=:file AND version >=:version ORDER BY version ASC LIMIT 1;

    """.trimIndent()

        val nativeFileTable = """
        CREATE TABLE NativeFileMapping (
          psiFile TEXT NOT NULL,
          jniClass TEXT NOT NULL,
          version INTEGER DEFAULT 0,
          path TEXT NOT NULL
        );

        CREATE INDEX NativeFileMapping_jniClass ON NativeFileMapping(psiFile,jniClass,version);

        selectAll:
        SELECT psiFile,jniClass,version,path FROM NativeFileMapping;

        count:
        SELECT count(*) FROM NativeFileMapping;

        getNativeFile:
        SELECT psiFile,jniClass,version,path FROM NativeFileMapping WHERE jniClass=:clazz AND version >=:version ORDER BY version ASC LIMIT 1;
    """.trimIndent()

        javaFileSQ.writeText(javaFileTable)
        nativeFileSQ.writeText(nativeFileTable)
    }

    enum class RepoType {
        AOSP_BACKUP,
        EXTERNAL_SKIA,
    }

    private fun RepoType.getAOSPRootPath(): String {
        return when (this) {
            RepoType.AOSP_BACKUP -> System.getenv("AOSP_BACKUP")
            RepoType.EXTERNAL_SKIA -> System.getenv("EXTERNAL_SKIA")
        }
    }

    private fun excludeDir(file: File): Boolean {
        val path = file.absolutePath
        return file.isDirectory ||
            path.contains("/tools/") ||
            path.contains("/apct-tests/") ||
            path.contains("/test-runner/") ||
            path.contains("/include/")
    }

    @Test
    fun generatorJava() {
        val javaSourceSQL = File("src/test/resources/JavaFileMapping.txt")
        javaSourceSQL.writeText("")
        AndroidVersion
            .getVersionSource(Source.AOSPMirror)
            .versionPairs()
            .entries
            .forEach { entry ->
                val branch = entry.value
                val javaSource = File("${RepoType.AOSP_BACKUP.getAOSPRootPath()}/$branch/java")
                val versionNumber = AndroidVersion.getBuildNumber(branch)
                javaSource
                    .walk()
                    .maxDepth(depth = 16)
                    .filter { file -> excludeDir(file).not() }
                    .forEach { file ->
                        // java key
                        val javaFileBuilder = StringBuilder()
                        readJavaFileLines(file, javaSource, versionNumber, javaFileBuilder)
                        if (javaFileBuilder.isNotEmpty()) {
                            javaSourceSQL.appendText(javaFileBuilder.toString())
                        }
                    }
            }
    }

    @Test
    fun generatorCpp() {
        val cppSourceSQL = File("src/test/resources/NativeFileMapping.txt")
        cppSourceSQL.writeText("")

        AndroidVersion
            .getVersionSource(Source.AOSPMirror)
            .versionPairs()
            .entries
            .forEach { entry ->
                val branch = entry.value
                val cppSource = File("${RepoType.AOSP_BACKUP.getAOSPRootPath()}/$branch/cpp")
                val versionNumber = AndroidVersion.getBuildNumber(branch)
                cppSource.walk()
                    .maxDepth(depth = 16)
                    .filter { file -> excludeDir(file).not() }
                    .filter { file -> file.readLines().any { line -> line.contains("JNINativeMethod") } }
                    .forEach { file ->
                        // AndroidRuntime::registerNativeMethods(env, "android/graphics/Canvas", gMethods, NELEM(gMethods));
                        // RegisterMethodsOrDie(env, "android/graphics/Canvas", gMethods, NELEM(gMethods));
                        // env->FindClass("android/drm/DrmManagerClient")
                        val jniClasses = file.readLines()
                            .mapNotNull { line ->
                                if (line.count { it == '"' } == 2) {
                                    try {
                                        when {
                                            // static const char* const kClassPathName = "android/media/AudioTrack";
                                            line.contains("kClassPathName = \"") -> {
                                                line.substring(
                                                    startIndex = line.indexOf("\"") + 1,
                                                    endIndex = line.lastIndexOf("\"")
                                                )
                                            }

                                            line.contains("env->FindClass(\"android") -> {
                                                // try find jni class
                                                val archer = "env->FindClass(\""
                                                val className = line.substring(
                                                    startIndex = line.indexOf(archer) + archer.length,
                                                    endIndex = line.indexOf("\")")
                                                )
                                                val shortName = className.substring(className.lastIndexOf("/") + 1)
                                                val getCppName = file.nameWithoutExtension
                                                if (getCppName.endsWith(shortName, ignoreCase = true)) {
                                                    className
                                                } else {
                                                    // println("line:$line\nclassName:$className getCppName:$getCppName shortName:$shortName")
                                                    null
                                                }
                                            }

                                            line.contains("registerNativeMethods(") -> {
                                                line.substring(
                                                    startIndex = line.indexOf("\"") + 1,
                                                    endIndex = line.lastIndexOf("\"")
                                                )
                                            }

                                            line.contains("RegisterMethodsOrDie(") -> {
                                                line.substring(
                                                    startIndex = line.indexOf("\"") + 1,
                                                    endIndex = line.lastIndexOf("\"")
                                                )
                                            }

                                            else -> null
                                        }
                                    } catch (throwable: Throwable) {
                                        println("crash file:$file line:$line ")
                                        throw throwable
                                    }
                                } else {
                                    null
                                }
                            }

                        // try to record native file
                        //INSERT INTO NativeFileMapping (psiFile,version,path)
                        //VALUES ("android/graphics/Bitmap.cpp",29,"core/jni/android/graphics/Bitmap.cpp");
                        val nativePath = file.parent.replace(cppSource.path, "") + "/" + file.name
                        jniClasses.forEach { jniClass ->
                            if (jniClass.isNotEmpty()) {
                                cppSourceSQL.appendText(
                                    "INSERT INTO NativeFileMapping (psiFile,jniClass,version,path) VALUES (\"${file.name}\",\"$jniClass\",$versionNumber,\"$nativePath\");\n"
                                )
                            } else {
                                println("jniClass:$jniClass version:$versionNumber path:$nativePath ")
                            }
                        }
                    }
            }
    }

    @Test
    fun writeDataSetToDB() {
        val javaSourceSQL = File("src/test/resources/JavaFileMapping.txt")
        val cppSourceSQL = File("src/test/resources/NativeFileMapping.txt")

        // sqlite db
        try {
            DriverManager.getConnection("jdbc:sqlite:src/main/resources/db/QuickRefDB.db").use { connection ->
                connection.createStatement().use { statement ->
                    statement.queryTimeout = 30 // set timeout to 30 sec.
                    // create a new table

                    statement.executeUpdate("DROP TABLE JavaFileMapping;")
                    statement.executeUpdate("DROP TABLE NativeFileMapping;")

                    statement.executeUpdate(
                        """
                        CREATE TABLE IF NOT EXISTS JavaFileMapping (
                            psiFile TEXT NOT NULL,
                            version INTEGER DEFAULT 0,
                            path TEXT NOT NULL
                        );
                        """.trimIndent()
                    )
                    statement.executeUpdate(
                        """
                        CREATE INDEX IF NOT EXISTS JavaFileMapping_path ON JavaFileMapping(psiFile,version);
                        """.trimIndent()
                    )
                    statement.executeUpdate(
                        """
                        CREATE TABLE IF NOT EXISTS NativeFileMapping (
                          psiFile TEXT NOT NULL,
                          jniClass TEXT NOT NULL,
                          version INTEGER DEFAULT 0,
                          path TEXT NOT NULL
                        );
                        """.trimIndent()
                    )
                    statement.executeUpdate(
                        """
                        CREATE INDEX IF NOT EXISTS NativeFileMapping_jniClass ON NativeFileMapping(psiFile,version,path);
                        """.trimIndent()
                    )
                }
                connection.createStatement().use { statement ->
                    javaSourceSQL.readLines().forEach { line ->
                        statement.executeUpdate(line)
                    }
                    cppSourceSQL.readLines().forEach { line ->
                        statement.executeUpdate(line)
                    }
                }
                connection.createStatement().use { statement ->
                    statement.executeUpdate(
                        """
                        DELETE FROM JavaFileMapping AS a
                        WHERE(
                            a.psiFile,
                            a.version,
                            a.path
                        ) IN (SELECT psiFile, version, path FROM JavaFileMapping GROUP BY psiFile, version, path HAVING count(*) > 1)
                        AND rowid NOT IN (SELECT min (rowid) FROM JavaFileMapping GROUP BY psiFile, version, path HAVING count(*)>1);
                    """.trimIndent()
                    )
                    statement.executeUpdate(
                        """
                        DELETE FROM NativeFileMapping AS a
                            WHERE(
                                a.psiFile,
                                a.jniClass,
                                a.version,
                                a.path
                            ) IN (SELECT psiFile, jniClass, version, path FROM NativeFileMapping GROUP BY psiFile, jniClass, version, path HAVING count(*) > 1)
                        AND rowid NOT IN (SELECT min (rowid) FROM NativeFileMapping GROUP BY psiFile, jniClass, version, path HAVING count(*)>1);
                    """.trimIndent()
                    )
                }
                connection.createStatement().use { statement ->
                    statement.executeUpdate("VACUUM;")
                }
            }
        } catch (e: SQLException) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            e.printStackTrace(System.err)
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
}
