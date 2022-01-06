package com.quickref.plugin.download

import com.intellij.openapi.application.ApplicationManager
import com.quickref.plugin.download.engine.GithubAOSPDownload
import com.quickref.plugin.download.engine.SourceGraphDownload
import com.quickref.plugin.download.engine.XrefDownload
import com.quickref.plugin.entity.DownloadTask
import java.io.File

object DownloadManager {

    private val engines = listOf(SourceGraphDownload(), XrefDownload(), GithubAOSPDownload())

    fun downloadFile(
        downloadTasks: Array<DownloadTask>,
        result: DownloadResult,
        isSync: Boolean,
        sameTarget: Boolean = false
    ) {
        val runnable = Runnable {
            val results: HashMap<String, File> = HashMap()
            var error: Throwable? = null
            // engine's total result.
            engines.sorted().forEach { engine ->
                try {
                    val startTime = System.currentTimeMillis()
                    val downloadResult: HashMap<String, File> = engine.onDownload(downloadTasks, sameTarget)
                    val costTime = System.currentTimeMillis() - startTime
                    // success = result's size == task's size
                    val success = downloadResult.size == downloadTasks.size
                    engine.updatePriority(success, costTime)
                    results.putAll(downloadResult)
                    if (success) {
                        return@forEach
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    error = e
                }
            }
            if (sameTarget && results.isNotEmpty()) {
                result.onSuccess(results)
            } else if (results.size == downloadTasks.size) {
                result.onSuccess(results)
            } else {
                result.onFailure("download fail:" + error?.message, error)
            }
        }

        if (isSync) {
            runnable.run()
        } else {
            ApplicationManager.getApplication().executeOnPooledThread(runnable)
        }
    }
}
