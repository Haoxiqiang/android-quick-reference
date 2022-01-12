package com.quickref.plugin.download.inteface

import com.intellij.openapi.progress.ProgressIndicator
import com.quickref.plugin.download.DownloadTask
import java.io.File

interface IDownload {
    fun onDownload(progressIndicator: ProgressIndicator?, tasks: Array<DownloadTask>): HashMap<String, File>
}
