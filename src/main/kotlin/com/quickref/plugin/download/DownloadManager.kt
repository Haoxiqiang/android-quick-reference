package com.quickref.plugin.download

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.quickref.plugin.download.engine.GithubAOSPDownload
import com.quickref.plugin.download.engine.SourceGraphDownload
import com.quickref.plugin.download.engine.XrefDownload
import java.io.File

object DownloadManager {

    private val engines = listOf(SourceGraphDownload(), XrefDownload(), GithubAOSPDownload())

    fun downloadFile(
        progressIndicator: ProgressIndicator?,
        downloadTasks: Array<DownloadTask>,
        result: DownloadResult,
        isSync: Boolean
    ) {
        val runnable = Runnable {
            val results: HashMap<String, File> = HashMap()
            // engine's total result.
            val error = runTask(progressIndicator, downloadTasks, results)
            if (results.isNotEmpty()) {
                result.onSuccess(results)
            } else {
                result.onFailure("download fail:" + error?.message, error)
            }
        }

        if (isSync) {
            runnable.run()
        } else {
            ApplicationManager.getApplication().runReadAction(runnable)
        }
    }

    private fun runTask(
        progressIndicator: ProgressIndicator?,
        downloadTasks: Array<DownloadTask>,
        results: HashMap<String, File>,
    ): Throwable? {
        var error: Throwable? = null
        engines.sorted().forEach { engine ->
            try {
                val startTime = System.currentTimeMillis()
                val downloadResult: HashMap<String, File> =
                    engine.onDownload(progressIndicator, downloadTasks)
                val costTime = System.currentTimeMillis() - startTime
                // success = result's size == task's size
                val success = downloadResult.size == downloadTasks.size
                engine.updatePriority(success, costTime)
                results.putAll(downloadResult)
                if (success) {
                    return error
                }
            } catch (e: Exception) {
                e.printStackTrace()
                error = e
            }
        }
        return error
    }
}
