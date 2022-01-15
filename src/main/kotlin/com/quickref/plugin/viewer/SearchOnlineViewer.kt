package com.quickref.plugin.viewer

import com.intellij.ide.BrowserUtil

/**
 * search online by google
 */
object SearchOnlineViewer {

    @Suppress("unused")
    private const val googleSourceOptions = "q=site%3Aandroid.googlesource.com"

    @Suppress("unused")
    private const val githubSourceOptions = "q=site%3github.com"

    private fun String.keywordTrim(): String {
        return trim { it <= ' ' }.replace(regex = "\\s+".toRegex(), "+")
    }

    fun googleSearch(keyword: String) {
        val url = "https://google.com/search?q=${keyword.keywordTrim()}"
        BrowserUtil.browse(url)
    }

    fun bingSearch(keyword: String) {
        val url = "https://www.bing.com/search?q=${keyword.keywordTrim()}"
        BrowserUtil.browse(url)
    }

    fun githubSearch(keyword: String) {
        val url = "https://github.com/search?q=${keyword.keywordTrim()}"
        BrowserUtil.browse(url)
    }

    fun stackoverflowSearch(keyword: String) {
        val url = "https://stackoverflow.com/search?q=${keyword.keywordTrim()}"
        BrowserUtil.browse(url)
    }

    fun codeSearch(keyword: String) {
        val url = "https://stackoverflow.com/search?q=${keyword.keywordTrim()}"
        BrowserUtil.browse(url)
    }
}
