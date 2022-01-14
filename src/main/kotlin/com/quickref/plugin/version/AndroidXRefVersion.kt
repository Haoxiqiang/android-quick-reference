package com.quickref.plugin.version

class AndroidXRefVersion : Version() {

    private val androidXRefVersions = linkedMapOf(
        Pair("android-9.0.0", "9.0.0_r3"),
        Pair("android-8.1.0", "8.1.0_r33"),
        Pair("android-8.0.0", "8.0.0_r4"),
        Pair("android-7.1.2", "7.1.2_r36"),
        // Pair("android-7.1.1", "7.1.1_r6"),
        Pair("android-7.0.0", "7.0.0_r1"),
        Pair("android-6.0.1", "6.0.1_r10"),
        // Pair("android-6.0.0", "6.0.0_r5"),
        // Pair("android-6.0.0", "6.0.0_r1"),
        Pair("android-5.1.1", "5.1.1_r6"),
        // Pair("android-5.1.0", "5.1.0_r1"),
        Pair("android-5.0.0", "5.0.0_r2"),
        //  Pair("android-4.4.4", "4.4.4_r1"),
        //  Pair("android-4.4.3", "4.4.3_r1.1"),
        //  Pair("android-4.4.2", "4.4.2_r2"),
        //  Pair("android-4.4.2", "4.4.2_r1"),
        //  Pair("android-4.4", "4.4"),
        //  Pair("android-4.3", "4.3"),
        //  Pair("android-4.2.2", "4.2.2"),
        //  Pair("android-4.2", "4.2"),
        //  Pair("android-4.1.2", "4.1.2"),
        //  Pair("android-4.1.1", "4.1.1"),
        //  Pair("android-4.0.4", "4.0.4"),
        //  Pair("android-4.0.3", "4.0.3"),
        //  Pair("android-2.3.7", "2.3.7"),
        //  Pair("android-2.3.6", "2.3.6"),
        //  Pair("android-2.2.3", "2.2.3"),
        //  Pair("android-2.1", "2.1"),
        //  Pair("android-1.6", "1.6"),
    )

    override fun versionPairs(): HashMap<String, String> {
        return androidXRefVersions
    }
}
