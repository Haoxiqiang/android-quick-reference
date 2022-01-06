package com.quickref.plugin.download.inteface

import com.intellij.platform.templates.github.DownloadUtil
import com.quickref.plugin.PluginLogger
import com.quickref.plugin.entity.DownloadTask
import com.quickref.plugin.version.AndroidVersion
import com.quickref.plugin.version.Source
import java.io.File
import java.io.IOException
import kotlin.math.abs

abstract class FileDownload(private val source: Source) : IDownload, Comparable<FileDownload> {
    // 下载优先级
    private var priority = 0

    // 平均时间
    private var costTimeAverage: Long = 0

    override fun onDownload(tasks: Array<DownloadTask>, sameTarget: Boolean): HashMap<String, File> {
        val map: HashMap<String, File> = HashMap()
        for (task in tasks) {

            val rawFile = task.rawFile()
            val key = task.key()
            if (rawFile.exists()) {
                map[key] = rawFile
                // if one task success, all task finish.
                if (sameTarget) {
                    break
                }
                continue
            }

            val version = task.versionName
            if (isVersionSupport(version)) {
                // convert branch version
                val branch = AndroidVersion.getVersionSource(source).versionBranch(version)
                val url: String = createDownloadURL(branch, task.path)
                try {
                    DownloadUtil.downloadAtomically(null, url, rawFile)
                    PluginLogger.debug("$url download success and save to ${rawFile.absolutePath}")
                } catch (e: IOException) {
                    PluginLogger.error("$url download failed.\n${e.message}")
                }

                if (rawFile.exists()) {
                    map[key] = rawFile
                    // if one task success, all task finish.
                    if (sameTarget) {
                        break
                    }
                }
            }
        }
        return map
    }

    private fun isVersionSupport(version: String): Boolean {
        return AndroidVersion.getVersionSource(source = source).isSupport(version)
    }

    abstract fun createDownloadURL(version: String, path: String): String

    /**
     * 更新优先级
     *
     * @param isSuccess 是否下载成功
     * @param costTime  下载花费时间
     */
    fun updatePriority(isSuccess: Boolean, costTime: Long) {
        PluginLogger.debug(
            "result:" + isSuccess + ", costTime:" + costTime + ", class:" + javaClass
                + ", priority:" + priority + ", average:" + costTimeAverage
        )
        priority = if (isSuccess) priority + 1 else priority - 1
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
        return if (abs(abs(priority) - abs(other.priority)) < 3 && costTimeAverage > 0 && other.costTimeAverage > 0) {
            costTimeAverage.compareTo(other.costTimeAverage)
        } else -priority.compareTo(other.priority)
    }

    // 暂停机制, 失败次数过多，暂停使用
    fun enable(): Boolean {
        return priority >= -3
    }
}
