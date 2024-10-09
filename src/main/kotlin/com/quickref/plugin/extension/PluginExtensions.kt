package com.quickref.plugin.extension

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassOwner
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiVariable
import com.quickref.plugin.PluginLogger


fun AnActionEvent.guessFileName(): String {
    val element = getData(LangDataKeys.PSI_ELEMENT) ?: return ""
    // base on field,we check it whether a psiclass
    val name = element.containingFile.name
    val extension = if (name.endsWith(".class")) {
        if ("java".equals(element.containingFile.originalFile.language.id, true)) {
            ".java"
        } else {
            ".kt"
        }
    } else {
        name.substring(name.lastIndexOf("."))
    }

    return name.substring(0, name.lastIndexOf(".")) + extension
}

//private fun tryGetVarPackageName(e: AnActionEvent, element: PsiElement, name: String): String {
//    if (element is PsiVariable) {
//        // origin file import analysis.
//        val psiFile = e.getData(LangDataKeys.PSI_FILE)
//        if (psiFile is PsiJavaFile) {
//            val imports =
//                psiFile.importList?.children?.filterIsInstance<PsiImportStatement>()?.filter { psiImportStatement ->
//                    psiImportStatement.qualifiedName == name
//                }
//            // in the list use the import
//            return "${
//                if (imports?.isNotEmpty() == true) {
//                    imports[0].qualifiedName
//                } else {
//                    // use the parent package
//                    psiFile.packageName
//                }
//            }.$name"
//        }
//    }
//    return ""
//}

fun AnActionEvent.packageName(): String? {
    val element = getData(LangDataKeys.PSI_FILE)
    if (element is PsiClassOwner) {
        return element.packageName
    }
    return null
}

fun PsiElement?.pathname(): String? {
    if (this == null) {
        return ""
    }
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
