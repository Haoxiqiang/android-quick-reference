package com.quickref.plugin.version

enum class Source {
    SourceGraph,
    CodeSearch,
    AndroidXRef,
    GithubAOSP,
}

object AndroidVersion {

    private val sources = linkedMapOf(
        Pair(Source.SourceGraph, SourceGraphVersion()),
        Pair(Source.CodeSearch, CodeSearchVersion()),
        Pair(Source.AndroidXRef, AndroidXRefVersion()),
        Pair(Source.GithubAOSP, GithubAOSPVersion()),
    )

    val merged by lazy { mergedAllSource() }

    fun getVersionSource(source: Source): Version {
        return sources[source]!!
    }

    private fun mergedAllSource(): List<String> {
        val versions = hashSetOf<String>()
        sources.forEach { entry ->
            entry.value.versionPairs().keys.forEach { version ->
                versions.add(version)
            }
        }
        return versions.toSortedSet().sortedWith(VersionComparator()).reversed()
    }

}
