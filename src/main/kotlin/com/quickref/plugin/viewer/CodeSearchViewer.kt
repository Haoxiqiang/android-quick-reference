package com.quickref.plugin.viewer

import com.intellij.ide.BrowserUtil
import com.quickref.plugin.PluginLogger
import com.quickref.plugin.extension.endsWithCLang
import com.quickref.plugin.version.AndroidVersion
import com.quickref.plugin.version.Source

object CodeSearchViewer {

    // Android 下载链接
    private const val VIEWER_PATH =
        "https://cs.android.com/android/platform/superproject/+/%s:frameworks/base/%s"

    private const val BASE_PATH = "core/java/%s"
    private const val GRAPHICS_PATH = "graphics/java/%s"
    private const val NATIVE_GRAPHICS_PATH = "core/jni/%s"

    fun open(version: String, path: String) {
        val csVersion = AndroidVersion.getVersionSource(Source.CodeSearch).versionBranch(version)
        val url = createViewerURL(csVersion, path)
        PluginLogger.debug("code search viewer url= $url")
        BrowserUtil.open(url)
    }

    private fun createViewerURL(version: String, path: String): String {
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
        return String.format(VIEWER_PATH, version, rawPath)
    }

    fun search(keyword: String) {
        val url = "https://cs.android.com/search?q=$keyword"
        PluginLogger.debug("code search viewer url= $url")
        BrowserUtil.open(url)
    }
}
