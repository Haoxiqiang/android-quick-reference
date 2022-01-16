package com.quickref.plugin.version

abstract class Version {

    abstract fun versionPairs(): Map<String, String>

    fun versionBranch(version: String): String {
        return versionPairs()[version]!!
    }

    fun isSupport(version: String): Boolean {
        return versionPairs().containsKey(version)
    }

    abstract fun isDownloadable(): Boolean
}
