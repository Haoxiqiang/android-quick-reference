package com.quickref.plugin.version

class AOSPMirrorVersion : Version() {

    private val aospVersions = linkedMapOf(
        Pair("android-15.0.0","android-15.0.0_r9"),
		Pair("android-14.0.0","android-14.0.0_r75"),
		Pair("android-13.0.0","android-13.0.0_r83"),
		Pair("android-12.1.0","android-12.1.0_r27"),
		Pair("android-12.0.0","android-12.0.0_r34"),
		Pair("android-11.0.0","android-11.0.0_r48"),
		Pair("android-10.0.0","android-10.0.0_r47"),
		Pair("android-9.0.0","android-9.0.0_r61"),
		Pair("android-8.1.0","android-8.1.0_r81"),
		Pair("android-8.0.0","android-8.0.0_r51"),
		Pair("android-7.1.2","android-7.1.2_r39"),
		Pair("android-7.1.1","android-7.1.1_r61"),
		Pair("android-7.1.0","android-7.1.0_r7"),
		Pair("android-7.0.0","android-7.0.0_r36"),
		Pair("android-6.0.1","android-6.0.1_r81"),
		Pair("android-6.0.0","android-6.0.0_r41"),
		Pair("android-5.1.1","android-5.1.1_r38"),
		Pair("android-5.1.0","android-5.1.0_r5"),
		Pair("android-5.0.2","android-5.0.2_r3"),
		Pair("android-5.0.1","android-5.0.1_r1"),
		Pair("android-5.0.0","android-5.0.0_r7"),
		Pair("android-4.4w","android-4.4w_r1"),
		Pair("android-4.4","android-4.4_r1.2.0.1"),
		Pair("android-4.4.4","android-4.4.4_r2.0.1"),
		Pair("android-4.4.3","android-4.4.3_r1.1.0.1"),
		Pair("android-4.4.2","android-4.4.2_r2.0.1"),
		Pair("android-4.4.1","android-4.4.1_r1.0.1"),
		Pair("android-4.3","android-4.3_r3.1"),
		Pair("android-4.3.1","android-4.3.1_r1"),
		Pair("android-4.2","android-4.2_r1"),
		Pair("android-4.2.2","android-4.2.2_r1.2"),
		Pair("android-4.2.1","android-4.2.1_r1.2"),
		Pair("android-4.1.2","android-4.1.2_r2.1"),
		Pair("android-4.1.1","android-4.1.1_r6.1"),
		Pair("android-4.0.4","android-4.0.4_r2.1"),
		Pair("android-4.0.3","android-4.0.3_r1.1"),
		Pair("android-4.0.2","android-4.0.2_r1"),
		Pair("android-4.0.1","android-4.0.1_r1.2"),
		Pair("android-3.2.4","android-3.2.4_r1"),
		Pair("android-2.3","android-2.3_r1"),
		Pair("android-2.3.7","android-2.3.7_r1"),
		Pair("android-2.3.6","android-2.3.6_r1"),
		Pair("android-2.3.5","android-2.3.5_r1"),
		Pair("android-2.3.4","android-2.3.4_r1"),
		Pair("android-2.3.3","android-2.3.3_r1.1"),
		Pair("android-2.3.2","android-2.3.2_r1"),
		Pair("android-2.3.1","android-2.3.1_r1"),
		Pair("android-2.2","android-2.2_r1.3"),
		Pair("android-2.2.3","android-2.2.3_r2.1"),
		Pair("android-2.2.2","android-2.2.2_r1"),
		Pair("android-2.2.1","android-2.2.1_r2"),
		Pair("android-2.1","android-2.1_r2.1s"),
		Pair("android-2.0","android-2.0_r1"),
		Pair("android-2.0.1","android-2.0.1_r1"),
		Pair("android-1.6","android-1.6_r2"),
    )

    override fun versionPairs(): HashMap<String, String> {
        return aospVersions
    }

    override fun isDownloadable(): Boolean {
        return true
    }
}
