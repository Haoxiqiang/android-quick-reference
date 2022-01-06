package com.quickref.plugin.widget

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.JBPopupFactory.ActionSelectionAid
import com.intellij.openapi.ui.popup.ListPopup

class AndroidVersionsPopView(private val actionEvent: AnActionEvent) {

    private var listPopup: ListPopup? = null

    fun show(title: String?, versions: List<String>, listener: (position: Int, version: String) -> Unit) {
        val group = DefaultActionGroup()
        versions.forEachIndexed { index, version ->
            group.add(VersionItemAction(index, version, listener))
        }
        listPopup = JBPopupFactory.getInstance()
            .createActionGroupPopup(
                title,
                group,
                actionEvent.dataContext,
                ActionSelectionAid.SPEEDSEARCH,
                true,
                null,
                -1,
                null,
                "unknown"
            )
        show()
    }

    fun dispose() {
        listPopup?.dispose()
    }

    private fun show() {
        if (listPopup == null) {
            return
        }
        val project = actionEvent.project
        if (project != null) {
            listPopup?.showCenteredInCurrentWindow(project)
        } else {
            listPopup?.showInBestPositionFor(actionEvent.dataContext)
        }
    }

    private class VersionItemAction(
        private val index: Int,
        private val value: String,
        private val listener: (position: Int, value: String) -> Unit
    ) : AnAction(value), DumbAware {
        override fun actionPerformed(anActionEvent: AnActionEvent) {
            listener.invoke(index, value)
        }
    }
}
