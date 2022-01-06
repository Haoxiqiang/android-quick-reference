package com.quickref.plugin.entity

import com.quickref.plugin.App
import java.io.File

data class DownloadTask(
    val path: String,
    val versionName: String,
) {
    fun rawFile(): File {
        val dir = File(App.CACHE_DIR, versionName)
        return File(dir, path)
    }

    fun key(): String {
        return versionName + File.separator + path
    }
}
