package com.quickref.plugin.viewer

import com.intellij.ide.BrowserUtil
import com.quickref.plugin.PluginLogger
import com.quickref.plugin.version.AndroidVersion
import com.quickref.plugin.version.Source

object CodeSearchViewer {

    // Android 下载链接
    private const val VIEWER_PATH =
        "https://cs.android.com/android/platform/superproject/+/%s:frameworks/base/%s"

    fun open(version: String, path: String) {
        val csVersion = AndroidVersion.getVersionSource(Source.AOSPMirror).versionBranch(version)
        val url = createViewerURL(csVersion, path)
        PluginLogger.debug("code search viewer url= $url")
        BrowserUtil.open(url)
    }

    private fun createViewerURL(version: String, path: String): String {
        return String.format(VIEWER_PATH, version, path)
    }

    fun search(keyword: String) {
        val url = "https://cs.android.com/search?q=$keyword"
        PluginLogger.debug("code search viewer url= $url")
        BrowserUtil.open(url)
    }
}
