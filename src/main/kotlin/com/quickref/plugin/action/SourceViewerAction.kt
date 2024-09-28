package com.quickref.plugin.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.ui.showOkNoDialog
import com.quickref.plugin.App
import com.quickref.plugin.Notifier
import com.quickref.plugin.download.DownloadTask
import com.quickref.plugin.extension.downloadFile
import com.quickref.plugin.extension.guessFileName
import com.quickref.plugin.extension.isAndroidFrameworkClass
import com.quickref.plugin.extension.isSupport
import com.quickref.plugin.extension.openFileInEditor
import com.quickref.plugin.extension.pathname
import com.quickref.plugin.version.AndroidVersion
import com.quickref.plugin.viewer.CodeSearchViewer
import com.quickref.plugin.widget.AndroidVersionsPopView

class SourceViewerAction : BaseAction() {

    override fun update(e: AnActionEvent) {
        val psiElement = e.getData(LangDataKeys.PSI_ELEMENT)
        e.presentation.isVisible =
            psiElement.isSupport() && psiElement.pathname().isAndroidFrameworkClass() && e.project != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val fileName = e.guessFileName()

        AndroidVersionsPopView(e)
            .show("Choose $fileName Version", AndroidVersion.sourceDownloadableVersions)
            { _, version ->
                val task = DownloadTask(fileName, version)
                project.downloadFile(arrayOf(task),
                    success = { output ->
                        val downloadFile = output[task.key()]
                        if (downloadFile == null) {
                            Notifier.errorNotification(project, "Error: Download $task")
                            return@downloadFile
                        }
                        project.openFileInEditor(downloadFile)
                    },
                    failed = { _, _ ->
                        // all download failed, use cs search.
                        if (showOkNoDialog(
                                title = App.AppTitle,
                                message = "Download source failed. Go to CodeSearch?",
                                project = project
                            )
                        ) {
                            CodeSearchViewer.search(fileName)
                        }
                    })
            }
    }
}
