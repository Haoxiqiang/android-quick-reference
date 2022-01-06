package com.quickref.plugin.download.engine

import com.quickref.plugin.download.inteface.FileDownload
import com.quickref.plugin.extension.endsWithCLang
import com.quickref.plugin.version.Source

class GithubAOSPDownload : FileDownload(Source.GithubAOSP) {

    companion object {
        // https://github.com/aosp-mirror/platform_frameworks_base/raw/lollipop-mr1-release/core/java/android/app/Activity.java
        private const val DOWNLOAD_PATH =
            "https://github.com/aosp-mirror/platform_frameworks_base/raw/raw/%s"

        private const val BASE_PATH = "core/java/%s"

        private const val GRAPHICS_PATH = "graphics/java/%s"

        private const val NATIVE_GRAPHICS_PATH = "core/jni/%s"
    }

    override fun createDownloadURL(version: String, path: String): String {
        val rawPath = if (path.startsWith("android/graphics")) {
            if (path.endsWithCLang()) {
                String.format(NATIVE_GRAPHICS_PATH, path)
            } else {
                String.format(GRAPHICS_PATH, path)
            }
        } else {
            if (path.endsWithCLang()) {
                String.format(NATIVE_GRAPHICS_PATH, path)
            } else {
                String.format(BASE_PATH, path)
            }
        }
        return String.format(DOWNLOAD_PATH, version, rawPath)
    }


}
