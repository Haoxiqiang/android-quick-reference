package com.quickref.plugin.extension

import java.io.File

fun String?.toFileRef(): String {
    if (isNullOrEmpty()) {
        return ""
    }
    return replace(".", File.separator)
}


// generate by link: https://developer.android.google.cn/reference/packages
private val ANDROID_REFERENCE_PREFIX = listOf(
    "android.",
    // "android.arch",
    "androidx.",
    "org.chromium.support_lib_boundary",
    "com.google.android",
    "com.android.build",
    "com.android.support",
    // "android.support.test",
    // google play service.
    "com.android.billingclient.api",
    // "com.google.android.play",
    "dalvik",
    "java.",
    "javax.",
    "junit.framework",
    "junit.runner",
    // strictly
    "org.apache.http.conn",
    // "org.apache.http.conn.scheme",
    // "org.apache.http.conn.ssl",
    "org.apache.http.params",
    "org.json",
    "org.w3c.dom",
    "org.w3c.dom.ls",
    "org.xml.sax",
    // "org.xml.sax.ext",
    // "org.xml.sax.helpers",
    "org.xmlpull.v1",
    // "org.xmlpull.v1.sax2",
)

private val ANDROID_FRAMEWORK_PREFIX = listOf(
    "android.accessibilityservice",
    "android.accounts",
    "android.animation",
    "android.app",
    "android.apphibernation",
    "android.appwidget",
    "android.attention",
    "android.bluetooth",
    "android.companion",
    "android.content",
    "android.database",
    "android.ddm",
    "android.debug",
    "android.gesture",
    "android.graphics",
    "android.hardware",
    "android.inputmethodservice",
    "android.metrics",
    "android.net",
    "android.nfc",
    "android.os",
    "android.permission",
    "android.permissionpresenterservice",
    "android.preference",
    "android.print",
    "android.printservice",
    "android.privacy",
    "android.provider",
    "android.rotationresolver",
    "android.se",
    "android.security",
    "android.service",
    "android.speech",
    "android.telephony",
    "android.text",
    "android.timezone",
    "android.tracing",
    "android.transition",
    "android.util",
    "android.uwb",
    "android.view",
    "android.webkit",
    "android.widget",
    "android.window",
    "com.android.ims.internal",//frameworks/base/core/java/com/android/ims/internal/
    "com.android.internal",//frameworks/base/core/java/com/android/internal/
    "com.android.server",
    "com.google.android",
)

fun String?.isAndroidClass(): Boolean {
    if (isNullOrEmpty()) {
        return false
    }
    return ANDROID_REFERENCE_PREFIX
        .any { prefix ->
            startsWith(prefix)
        }
}

fun String?.isAndroidFrameworkClass(): Boolean {
    if (isNullOrEmpty()) {
        return false
    }
    return ANDROID_FRAMEWORK_PREFIX
        .any { prefix ->
            startsWith(prefix)
        }
}

fun String.endsWithCLang(): Boolean {
    return endsWith(".cpp") || endsWith(".cc") || endsWith(".c")
}

