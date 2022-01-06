package com.quickref.plugin.download.engine

import com.quickref.plugin.download.inteface.FileDownload
import com.quickref.plugin.extension.endsWithCLang
import com.quickref.plugin.version.Source

class SourceGraphDownload : FileDownload(Source.SourceGraph) {

    companion object {
        // Android 下载链接
        private const val DOWNLOAD_PATH =
            "https://sourcegraph.com/github.com/aosp-mirror/platform_frameworks_base@%s/-/raw/%s"

        // https://sourcegraph.com/github.com/aosp-mirror/platform_frameworks_base@lollipop-mr1-release/-/raw/core/java/android/app/Activity.java
        private const val BASE_PATH = "core/java/%s"

        // https://sourcegraph.com/github.com/aosp-mirror/platform_frameworks_base@lollipop-mr1-release/-/raw/graphics/java/android/graphics/Bitmap.java
        private const val GRAPHICS_PATH = "graphics/java/%s"

        // https://sourcegraph.com/github.com/aosp-mirror/platform_frameworks_base@lollipop-mr1-release/-/raw/core/jni/android/graphics/Bitmap.cpp
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
