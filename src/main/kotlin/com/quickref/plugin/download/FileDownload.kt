package com.quickref.plugin.download

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.platform.templates.github.DownloadUtil
import com.quickref.plugin.App
import com.quickref.plugin.PluginLogger
import com.quickref.plugin.download.inteface.IDownload
import com.quickref.plugin.extension.endsWithJava
import com.quickref.plugin.version.AndroidVersion
import com.quickref.plugin.version.Source
import java.io.File
import java.io.IOException
import java.util.Base64
import java.util.Locale
import kotlin.math.abs

abstract class FileDownload(private val source: Source) : IDownload, Comparable<FileDownload> {

    companion object {
        const val MAX_FAILED_COUNT = 3
    }

    // 下载优先级
    private var priority = 0

    // 平均时间
    private var costTimeAverage: Long = 0

    override fun onDownload(
        progressIndicator: ProgressIndicator?,
        tasks: Array<DownloadTask>,
    ): HashMap<String, File> {
        val map: HashMap<String, File> = HashMap()
        tasks.forEach { task ->

            val rawFile = task.rawFile()
            val key = task.key()
            if (rawFile.exists()) {
                map[key] = rawFile
                // if one task success, all task finish.
                return@forEach
            }

            val version = task.versionName

            if (rawFile.parentFile?.exists() == false) {
                rawFile.parentFile?.mkdirs()
            }

            if (isVersionSupport(version)) {
                // convert branch version
                val branch = AndroidVersion.getVersionSource(source).versionBranch(version)
                val url: String = createDownloadURL(version, branch, task.path)
                PluginLogger.debug("$url prepare downloading")

                try {
                    // need base64 decode
                    val needBase64Decode = url.endsWith("?format=TEXT")
                    if (needBase64Decode) {
                        // base64 decode
                        val base64File = File(rawFile.parent, rawFile.name + ".base64")
                        if (base64File.exists()) {
                            base64File.delete()
                        }
                        base64File.createNewFile()
                        DownloadUtil.downloadAtomically(progressIndicator, url, base64File)
                        // base64 decode base64File to rawFile
                        val base64Content = base64File.readText()
                        rawFile.writeBytes(Base64.getDecoder().decode(base64Content))
                        base64File.delete()
                    } else {
                        DownloadUtil.downloadAtomically(progressIndicator, url, rawFile)
                    }
                } catch (e: IOException) {
                    PluginLogger.error("$url download failed.\n${e.message}")
                }

                if (rawFile.exists()) {
                    map[key] = rawFile
                }
            }
        }
        return map
    }

    private fun isVersionSupport(version: String): Boolean {
        return AndroidVersion.getVersionSource(source = source).isSupport(version)
    }

    private fun createDownloadURL(version: String, branch: String, path: String): String {
        // "http://androidxref.com/%s/raw/%s/frameworks/base"
        // http://androidxref.com/9.0.0_r3/raw/frameworks/base/core/java/android/app/Activity.java
        // http://androidxref.com/7.1.1_r6/raw/frameworks/base/graphics/java/android/graphics/Bitmap.java
        // http://androidxref.com/7.1.1_r6/raw/frameworks/base/core/jni/android/graphics/Bitmap.cpp
        val rawPath = if (path.endsWithJava()) {
            val versionNumber = AndroidVersion.getBuildNumber(version).toLong()
            val javaPath = App.db.javaFileMappingQueries.getJavaFile(
                file = path, versionNumber
            ).executeAsOneOrNull()
            javaPath ?: path
        } else {
            // try get native method.
            path
        }
        return String.format(Locale.ENGLISH, baseDownloadURL(), branch, rawPath)
    }

    abstract fun baseDownloadURL(): String

    /**
     * 更新优先级
     *
     * @param isSuccess 是否下载成功
     * @param costTime  下载花费时间
     */
    fun updatePriority(isSuccess: Boolean, costTime: Long) {
        PluginLogger.debug(
            "$isSuccess -> costTime:" + costTime +
                ", priority:" + priority + ", average:" + costTimeAverage
        )
        priority = if (isSuccess) {
            priority + 1
        } else {
            priority - 1
        }
        if (isSuccess && costTime > 0) {
            costTimeAverage = if (costTimeAverage == 0L) {
                costTime
            } else {
                (costTimeAverage + costTime) / 2
            }
        }
    }

    override fun compareTo(other: FileDownload): Int {
        // > 0 是大数往后排
        return if (
            abs(abs(priority) - abs(other.priority)) < MAX_FAILED_COUNT
            && costTimeAverage > 0 && other.costTimeAverage > 0
        ) {
            costTimeAverage.compareTo(other.costTimeAverage)
        } else -priority.compareTo(other.priority)
    }

    // 暂停机制, 失败次数过多，暂停使用
    fun enable(): Boolean {
        return abs(priority) >= MAX_FAILED_COUNT
    }
}
