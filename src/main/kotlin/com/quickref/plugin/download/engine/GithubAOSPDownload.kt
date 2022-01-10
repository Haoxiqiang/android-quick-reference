package com.quickref.plugin.download.engine

import com.quickref.plugin.download.inteface.FileDownload
import com.quickref.plugin.version.Source

class GithubAOSPDownload : FileDownload(Source.GithubAOSP) {

    companion object {
        // https://github.com/aosp-mirror/platform_frameworks_base/raw/lollipop-mr1-release/core/java/android/app/Activity.java
        private const val DOWNLOAD_PATH =
            "https://github.com/aosp-mirror/platform_frameworks_base/raw/%s"
    }

    override fun baseDownloadURL(): String {
        return DOWNLOAD_PATH
    }
}
