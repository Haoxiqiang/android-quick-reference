package com.quickref.plugin.action

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.quickref.plugin.PluginLogger
import com.quickref.plugin.extension.isAndroidClass
import com.quickref.plugin.extension.isSupport
import com.quickref.plugin.extension.pathname
import com.quickref.plugin.extension.referencePage

/**
 * Refer to <a href="https://developer.android.google.cn/reference">Android Reference</a>
 *
 * https://developer.android.google.cn/reference/android/view/View.OnClickListener.html#onClick(android.view.View)
 * https://developer.android.google.cn/reference/android/app/Activity.html#onRestoreInstanceState(android.os.Bundle)
 */
class AndroidReferenceAction : BaseAction() {

    companion object {
        private const val REF_URL: String = "https://developer.android.google.cn/reference/"
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        val psiElement = e.getData(LangDataKeys.PSI_ELEMENT)
        e.presentation.isVisible = psiElement.isSupport() && psiElement.pathname().isAndroidClass()
    }

    override fun actionPerformed(e: AnActionEvent) {

        val element = e.getData(LangDataKeys.PSI_ELEMENT)

        val linkerBuilder = StringBuilder()
        if (element is PsiMethod) {
            // try to fill parameter
            val parameters = element.parameterList.parameters
            val paramsBuilder = StringBuilder("#" + element.name)
            paramsBuilder.append("(")
            for (i in parameters.indices) {
                val parameter = parameters[i]
                paramsBuilder.append(parameter.type.canonicalText)
                if (i < parameters.size - 1) {
                    paramsBuilder.append(",%20")
                }
            }
            paramsBuilder.append(")")
            linkerBuilder.append(element.containingClass.referencePage() + paramsBuilder.toString())
        } else if (element is PsiClass) {
            linkerBuilder.append(element.referencePage())
        }

        val url = "${REF_URL}$linkerBuilder"
        PluginLogger.debug("linkUrl= $url")
        BrowserUtil.open(url)
    }

}
