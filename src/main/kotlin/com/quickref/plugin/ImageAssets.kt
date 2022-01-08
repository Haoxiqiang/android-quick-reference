package com.quickref.plugin

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.JBColor
import javax.swing.Icon

object ImageAssets {

    val ICON_JAVA = intellijLoad("/fileTypes/java.svg")
    val ICON_DIFF = intellijLoad("/actions/diff.svg")

    val NATIVE = load("/icons/language-cpp.svg")
    val GITHUB = load("/icons/findGit.svg")
    val GOOGLE = load("/icons/findGoogle.svg")
    val BING = load("/icons/findBing.svg")
    val CODESEARCH = load("/icons/csAndroid.svg")
    val STACKOVERFLOW = load("/icons/findStackOverflow.svg")

    fun isBright(): Boolean {
        return JBColor.isBright()
    }

    fun isDarkTheme(): Boolean {
        return isBright().not()
    }

    private fun load(path: String): Icon {
        return IconLoader.getIcon(path, ImageAssets::class.java)
    }

    private fun intellijLoad(path: String): Icon {
        return IconLoader.getIcon(path, AllIcons::class.java)
    }
}
