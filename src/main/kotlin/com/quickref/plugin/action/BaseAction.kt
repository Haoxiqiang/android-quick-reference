package com.quickref.plugin.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys

abstract class BaseAction : AnAction() {

    override fun update(e: AnActionEvent) {
        val hasElement = e.getData(LangDataKeys.PSI_ELEMENT) != null
        e.presentation.isEnabledAndVisible = hasElement
    }
}
