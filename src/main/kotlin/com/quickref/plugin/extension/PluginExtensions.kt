package com.quickref.plugin.extension

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassOwner
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiVariable
import com.quickref.plugin.PluginLogger

fun AnActionEvent.packageName(): String? {
    val element = getData(LangDataKeys.PSI_FILE)
    if (element is PsiClassOwner) {
        return element.packageName
    }
    return null
}

fun PsiElement.pathname(): String? {

    val element = this

    var name: String? = null

    when (element) {
        is PsiClass -> {
            name = if (element.containingClass != null) {
                // 排除内部类的情况
                element.containingClass?.qualifiedName
            } else {
                element.qualifiedName
            }
            PluginLogger.debug("PsiClass's class:$name")
        }
        is PsiMethod -> {
            val method = element.containingClass?.qualifiedName
            PluginLogger.debug("PsiMethod's method:${element.name}#$method")
            name = method
        }
        is PsiVariable -> {
            name = element.type.canonicalText
            // 去除泛型
            if (name.isNotEmpty()) {
                name = name.replace("<.*>".toRegex(), "")
                PluginLogger.debug("PsiVariable's text: pre:${element.type.canonicalText} after:$name")
            }
        }
        else -> {
            PluginLogger.debug("cls = " + element.javaClass)
        }
    }

    return name
}
