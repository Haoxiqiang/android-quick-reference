package com.quickref.plugin.config

@Suppress("MagicNumber")
fun QuickReferenceConfigStorage.isSupportVersion(version: Int): Boolean {
    return when (version) {
        31 -> enableAndroid31
        30 -> enableAndroid30
        29 -> enableAndroid29
        28 -> enableAndroid28
        27 -> enableAndroid27
        26 -> enableAndroid26
        25 -> enableAndroid25
        24 -> enableAndroid24
        23 -> enableAndroid23
        22 -> enableAndroid22
        21 -> enableAndroid21
        20 -> enableAndroid20
        19 -> enableAndroid19
        18 -> enableAndroid18
        17 -> enableAndroid17
        16 -> enableAndroid16
        15 -> enableAndroid15
        14 -> enableAndroid14
        13 -> enableAndroid13
        10 -> enableAndroid10
        8 -> enableAndroid08
        7 -> enableAndroid07
        6 -> enableAndroid06
        5 -> enableAndroid05
        else -> false
    }
}
