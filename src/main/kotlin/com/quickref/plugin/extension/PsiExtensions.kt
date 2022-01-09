package com.quickref.plugin.extension

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod


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
