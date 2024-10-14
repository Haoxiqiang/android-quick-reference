package com.quickref.plugin.widget

import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.JBPopupFactory.ActionSelectionAid
import com.intellij.openapi.ui.popup.ListPopup

class AndroidVersionsPopView(
    private val project: Project?,
    private val dataContext: DataContext,
) {

    private var listPopup: ListPopup? = null

    fun show(title: String?, versions: List<String>, listener: (position: Int, version: String) -> Unit) {
        val group = DefaultActionGroup()
        versions.forEachIndexed { index, version ->
            group.add(VersionItemAction(index, version, listener))
        }

        listPopup = JBPopupFactory.getInstance()
            .createActionGroupPopup(
                " $title ",
                group,
                dataContext,
                ActionSelectionAid.SPEEDSEARCH,
                false,
                ActionPlaces.POPUP
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
        if (project != null) {
            listPopup?.showCenteredInCurrentWindow(project)
        } else {
            listPopup?.showInBestPositionFor(dataContext)
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
