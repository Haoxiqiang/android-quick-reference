package com.quickref.plugin.download.engine

import com.quickref.plugin.download.FileDownload
import com.quickref.plugin.version.Source

class AOSPDownload : FileDownload(Source.AOSPMirror) {

    companion object {
        // curl "https://android.googlesource.com/platform/frameworks/base.git/+/refs/tags/
        // android-14.0.0_r69/identity/java/android/security/identity/AccessControlProfile.java
        // ?format=TEXT"| base64 --decode > AccessControlProfile.java
        private const val DOWNLOAD_PATH =
            "https://android.googlesource.com/platform/frameworks/base.git/+/refs/tags/%s/%s?format=TEXT"
    }

    override fun baseDownloadURL(): String {
        return DOWNLOAD_PATH
    }
}
