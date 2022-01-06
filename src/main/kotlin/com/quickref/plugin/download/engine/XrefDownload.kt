package com.quickref.plugin.download.engine

import com.quickref.plugin.download.inteface.FileDownload
import com.quickref.plugin.extension.endsWithCLang
import com.quickref.plugin.version.Source

class XrefDownload : FileDownload(Source.AndroidXRef) {

    companion object {
        // Android 下载链接
        private const val DOWNLOAD_PATH = "http://androidxref.com/%s/raw/%s"

        // http://androidxref.com/9.0.0_r3/raw/frameworks/base/core/java/android/app/Activity.java
        private const val BASE_PATH = "frameworks/base/core/java/%s"

        // http://androidxref.com/7.1.1_r6/raw/frameworks/base/graphics/java/android/graphics/Bitmap.java
        private const val GRAPHICS_PATH = "frameworks/base/graphics/java/%s"

        // http://androidxref.com/7.1.1_r6/raw/frameworks/base/core/jni/android/graphics/Bitmap.cpp
        private const val NATIVE_GRAPHICS_PATH = "frameworks/base/core/jni/%s"
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
