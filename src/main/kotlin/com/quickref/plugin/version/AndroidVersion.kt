package com.quickref.plugin.version

enum class Source {
    SourceGraph,
    CodeSearch,
    AndroidXRef,
    GithubAOSP,
}

object AndroidVersion {

    private val sources = linkedMapOf(
        Pair(Source.SourceGraph, GithubAOSPVersion()),
        Pair(Source.GithubAOSP, GithubAOSPVersion()),
        Pair(Source.CodeSearch, CodeSearchVersion()),
        Pair(Source.AndroidXRef, AndroidXRefVersion()),
    )

    val merged by lazy { mergedAllSource() }

    fun getVersionSource(source: Source): Version {
        return sources[source]!!
    }

    fun getBuildNumber(version: String): Int {
        val lastIndex = version.indexOf("_")
        val rawVersion = if (lastIndex > 0) {
            version.subSequence(0, lastIndex)
        } else {
            version
        }
        return androidBuildVersions[rawVersion].toString().toIntOrNull() ?: 0
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

    // https://source.android.com/setup/start/build-numbers
    @Suppress("MagicNumber")
    private val androidBuildVersions = linkedMapOf(
        Pair("android-12.0.0", 31),
        Pair("android-11.0.0", 30),
        Pair("android-10.0.0", 29),
        Pair("android-9.0.0", 28),
        Pair("android-8.1.0", 27),
        Pair("android-8.0.0", 26),
        Pair("android-7.1.2", 25),
        Pair("android-7.1.1", 25),
        Pair("android-7.1.0", 25),
        Pair("android-7.0.0", 24),
        Pair("android-6.0.1", 23),
        Pair("android-6.0.0", 23),
        Pair("android-5.1.1", 22),
        Pair("android-5.1.0", 22),
        Pair("android-5.0.2", 21),
        Pair("android-5.0.1", 21),
        Pair("android-5.0.0", 21),
        Pair("android-4.4w", 20),
        Pair("android-4.4", 19),
        Pair("android-4.4.4", 19),
        Pair("android-4.4.3", 19),
        Pair("android-4.4.2", 19),
        Pair("android-4.4.1", 19),
        Pair("android-4.3", 18),
        Pair("android-4.3.1", 18),
        Pair("android-4.2.2", 17),
        Pair("android-4.2.1", 17),
        Pair("android-4.2", 17),
        Pair("android-4.1.2", 16),
        Pair("android-4.1.1", 16),
        Pair("android-4.0.4", 15),
        Pair("android-4.0.3", 15),
        Pair("android-4.0.2", 14),
        Pair("android-4.0.1", 14),
        Pair("android-3.2.4", 13),
        Pair("android-3.2.3", 13),
        Pair("android-3.2.2", 13),
        Pair("android-3.2.1", 13),
        Pair("android-3.1", 12),
        Pair("android-3.0", 11),
        Pair("android-2.3.7", 10),
        Pair("android-2.3.6", 10),
        Pair("android-2.3.5", 10),
        Pair("android-2.3.4", 10),
        Pair("android-2.3.3", 10),
        Pair("android-2.3.2", 10),
        Pair("android-2.3.1", 10),
        Pair("android-2.3", 9),
        Pair("android-2.2.3", 8),
        Pair("android-2.2.2", 8),
        Pair("android-2.2.1", 8),
        Pair("android-2.2", 8),
        Pair("android-2.1", 7),
        Pair("android-2.0.1", 6),
        Pair("android-2.0", 5),
        Pair("android-1.6", 4),
    )
}
