package com.quickref.plugin.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.quickref.plugin.Notifier
import com.quickref.plugin.PluginLogger
import com.quickref.plugin.download.DownloadTask
import com.quickref.plugin.extension.diff
import com.quickref.plugin.extension.downloadFile
import com.quickref.plugin.extension.guessFileName
import com.quickref.plugin.extension.isAndroidFrameworkClass
import com.quickref.plugin.extension.isSupport
import com.quickref.plugin.extension.pathname
import com.quickref.plugin.version.AndroidVersion
import com.quickref.plugin.widget.AndroidVersionsPopView

class DiffSourceAction : BaseAction() {

    override fun update(e: AnActionEvent) {
        val psiElement = e.getData(LangDataKeys.PSI_ELEMENT)
        e.presentation.isVisible =
            psiElement.isSupport() && e.project != null && psiElement.pathname().isAndroidFrameworkClass()
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }


    override fun actionPerformed(e: AnActionEvent) {

        val fileName = e.guessFileName()
        val project = e.project ?: return

        val versions = AndroidVersion.mergedDownloadableSource()

        AndroidVersionsPopView(
            project = e.project,
            dataContext = e.dataContext
        )
            .show("Choose First Version", versions) { _, firstVersion ->
                AndroidVersionsPopView(
                    project = e.project,
                    dataContext = e.dataContext
                )
                    .show("Choose Second Version", versions) { _, secondVersion ->

                        PluginLogger.debug("diff $firstVersion...$secondVersion")

                        val task1 = DownloadTask(fileName, firstVersion)
                        val task2 = DownloadTask(fileName, secondVersion)

                        project.downloadFile(
                            arrayOf(task1, task2),
                            success = { output ->
                                val files = output.values.toMutableList()
                                PluginLogger.debug("DownloadResult=$files")
                                if (files.size < 2) {
                                    Notifier.errorNotification(project, "Error: Download $task1")
                                    return@downloadFile
                                }
                                project.diff(files[0], files[1])
                            },
                            failed = { _, _ ->
                            })

                    }
            }
    }
}
