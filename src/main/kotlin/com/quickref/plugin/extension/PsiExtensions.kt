package com.quickref.plugin.extension

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod


fun PsiElement?.isSupport(): Boolean {
    // must be a class
    if (this is PsiClass) {
        // check inner class
        if (containingClass != null) {
            return false
        }
        return this.name != "String"
    }
    return false
}

/**
 * 替换成 Developer 需要的包名
 * 如 android.view.View.OnClickListener 替换成 android/view/View.OnClickListener.html
 */
fun PsiClass?.referencePage(): String {
    if (this == null) {
        return ""
    }

    var classPackage = qualifiedName ?: return ""
    val topPackage: String = containingClass?.qualifiedName ?: ""
    if (topPackage.isEmpty()) {
        return classPackage.replace("\\.".toRegex(), "/") + ".html"
    }
    classPackage = classPackage.replaceFirst(
        topPackage.toRegex(),
        topPackage.replace(
            regex = "\\.".toRegex(),
            replacement = "/"
        )
    )
    return "$classPackage.html"
}

fun PsiMethod.isAndroidFrameworkClass(): Boolean {
    val packageName = containingClass?.qualifiedName
    return packageName.isAndroidFrameworkClass()
}
