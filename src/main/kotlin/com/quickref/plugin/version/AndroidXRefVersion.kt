package com.quickref.plugin.version

class AndroidXRefVersion : Version() {

    private val androidXRefVersions = linkedMapOf(
        Pair("9.0.0", "9.0.0_r3"),
        Pair("8.1.0", "8.1.0_r33"),
        Pair("8.0.0", "8.0.0_r4"),
        Pair("7.1.2", "7.1.2_r36"),
        Pair("7.1.1", "7.1.1_r6"),
        Pair("7.0.0", "7.0.0_r1"),
        Pair("6.0.1", "6.0.1_r10"),
        Pair("6.0.0", "6.0.0_r5"),
        Pair("6.0.0", "6.0.0_r1"),
        Pair("5.1.1", "5.1.1_r6"),
        Pair("5.1.0", "5.1.0_r1"),
        Pair("5.0.0", "5.0.0_r2"),
        Pair("4.4.4", "4.4.4_r1"),
        Pair("4.4.3", "4.4.3_r1.1"),
        Pair("4.4.2", "4.4.2_r2"),
        Pair("4.4.2", "4.4.2_r1"),
        Pair("4.4", "4.4"),
        Pair("4.3", "4.3"),
        Pair("4.2.2", "4.2.2"),
        Pair("4.2", "4.2"),
        Pair("4.1.2", "4.1.2"),
        Pair("4.1.1", "4.1.1"),
        Pair("4.0.4", "4.0.4"),
        Pair("4.0.3", "4.0.3"),
        Pair("2.3.7", "2.3.7"),
        Pair("2.3.6", "2.3.6"),
        Pair("2.2.3", "2.2.3"),
        Pair("2.1", "2.1"),
        Pair("1.6", "1.6"),
    )

    override fun versionPairs(): HashMap<String, String> {
        return androidXRefVersions
    }
}
