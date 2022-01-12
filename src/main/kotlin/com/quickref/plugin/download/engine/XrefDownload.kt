package com.quickref.plugin.download.engine

import com.quickref.plugin.download.FileDownload
import com.quickref.plugin.version.Source

class XrefDownload : FileDownload(Source.AndroidXRef) {

    companion object {
        // Android 下载链接
        private const val DOWNLOAD_PATH = "http://androidxref.com/%s/raw/%s/frameworks/base"
        // http://androidxref.com/9.0.0_r3/raw/frameworks/base/core/java/android/app/Activity.java
        // http://androidxref.com/7.1.1_r6/raw/frameworks/base/graphics/java/android/graphics/Bitmap.java
        // http://androidxref.com/7.1.1_r6/raw/frameworks/base/core/jni/android/graphics/Bitmap.cpp
    }

    override fun baseDownloadURL(): String {
        return DOWNLOAD_PATH
    }
}
