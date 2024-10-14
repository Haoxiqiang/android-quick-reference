package com.quickref.plugin.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.quickref.plugin.Notifier
import com.quickref.plugin.extension.pathname
import javax.swing.Icon

class KeywordSearchAction(
    text: String,
    description: String,
    icon: Icon,
    private val action: (keyword: String) -> Unit
) :
    AnAction(text, description, icon) {

    override fun actionPerformed(event: AnActionEvent) {

        val editor = event.getRequiredData(CommonDataKeys.EDITOR)
        var keyword: String? = editor.selectionModel.selectedText
        val fileName = event.getData(LangDataKeys.PSI_ELEMENT)?.pathname() ?: ""
        if (fileName.isEmpty()) {
            if (keyword.isNullOrEmpty()) {
                // if without selected
                val element = event.getData(LangDataKeys.PSI_ELEMENT)
                if (element == null) {
                    Notifier.infoNotification(event.project, "selected code empty")
                    return
                }
                val result = element.toString().split(":".toRegex()).toTypedArray()
                keyword = if (result.size == 2) {
                    result[1]
                } else {
                    element.toString()
                }
            }
        } else {
            keyword = fileName
        }

        // update action visible state.
        event.presentation.isEnabledAndVisible = keyword.isNotEmpty()
        action.invoke(keyword)
    }
}
