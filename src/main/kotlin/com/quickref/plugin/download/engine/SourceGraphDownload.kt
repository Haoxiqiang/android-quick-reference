package com.quickref.plugin.download.engine

import com.quickref.plugin.download.FileDownload
import com.quickref.plugin.version.Source

class SourceGraphDownload : FileDownload(Source.AOSPMirror) {

    companion object {
        // Android 下载链接
        private const val DOWNLOAD_PATH =
            "https://sourcegraph.com/github.com/aosp-mirror/platform_frameworks_base@%s/-/raw%s"

        // https://sourcegraph.com/github.com/aosp-mirror/platform_frameworks_base@lollipop-mr1-release/-/raw/core/java/android/app/Activity.java
        // https://sourcegraph.com/github.com/aosp-mirror/platform_frameworks_base@lollipop-mr1-release/-/raw/graphics/java/android/graphics/Bitmap.java
        // https://sourcegraph.com/github.com/aosp-mirror/platform_frameworks_base@lollipop-mr1-release/-/raw/core/jni/android/graphics/Bitmap.cpp
    }

    override fun baseDownloadURL(): String {
        return DOWNLOAD_PATH
    }
}
