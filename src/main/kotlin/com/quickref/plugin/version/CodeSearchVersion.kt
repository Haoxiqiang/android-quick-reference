package com.quickref.plugin.version

class CodeSearchVersion : Version() {

    private val codeSearchVersions = linkedMapOf(
        Pair("android-12.0.0", "android-12.0.0_r4"),
        Pair("android-11.0.0", "android-11.0.0_r9"),
        Pair("android-10.0.0", "android-10.0.0_r9"),
        Pair("android-9.0.0", "android-9.0.0_r9"),
        Pair("android-8.1.0", "android-8.1.0_r9"),
        Pair("android-8.0.0", "android-8.0.0_r9"),
        Pair("android-7.1.2", "android-7.1.2_r9"),
        Pair("android-7.1.1", "android-7.1.1_r9"),
        Pair("android-7.1.0", "android-7.1.0_r7"),
        Pair("android-7.0.0", "android-7.0.0_r7"),
        Pair("android-6.0.1", "android-6.0.1_r9"),
        Pair("android-6.0.0", "android-6.0.0_r7"),
        Pair("android-5.1.1", "android-5.1.1_r9"),
        Pair("android-5.1.0", "android-5.1.0_r5"),
        Pair("android-5.0.2", "android-5.0.2_r3"),
        Pair("android-5.0.1", "android-5.0.1_r1"),
        Pair("android-5.0.0", "android-5.0.0_r7.0.1"),
        Pair("android-4.4w", "android-4.4w_r1"),
        Pair("android-4.4", "android-4.4_r1.2.0.1"),
        Pair("android-4.4.4", "android-4.4.4_r2.0.1"),
        Pair("android-4.4.3", "android-4.4.3_r1.1.0.1"),
        Pair("android-4.4.2", "android-4.4.2_r2.0.1"),
        Pair("android-4.4.1", "android-4.4.1_r1.0.1"),
        Pair("android-4.3", "android-4.3_r2"),
        Pair("android-4.3.1", "android-4.3.1_r1"),
        Pair("android-4.2.2", "android-4.2.2_r1.2"),
        Pair("android-4.1.2", "android-4.1.2_r2.1"),
        Pair("android-4.1.1", "android-4.1.1_r6.1"),
        Pair("android-4.0.4", "android-4.0.4_r2.1"),
        Pair("android-4.0.3", "android-4.0.3_r1.1"),
        Pair("android-4.0.2", "android-4.0.2_r1"),
        Pair("android-4.0.1", "android-4.0.1_r1.2"),
        Pair("android-2.3.7", "android-2.3.7_r1"),
        Pair("android-2.3.6", "android-2.3.6_r1"),
        Pair("android-2.2.3", "android-2.2.3_r2.1"),
    )

    override fun versionPairs(): HashMap<String, String> {
        return codeSearchVersions
    }
}
