package com.quickref.plugin.download

import java.io.File

interface DownloadResult {
    fun onSuccess(output: HashMap<String, File>)
    fun onFailure(msg: String, throwable: Throwable?)
}
