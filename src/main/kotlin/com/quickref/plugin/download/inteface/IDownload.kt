package com.quickref.plugin.download.inteface

import com.quickref.plugin.entity.DownloadTask
import java.io.File

interface IDownload {
    fun onDownload(tasks: Array<DownloadTask>, sameTarget: Boolean): HashMap<String, File>
}
