package com.quickref.plugin.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.quickref.plugin.Notifier
import com.quickref.plugin.PluginLogger
import com.quickref.plugin.action.base.BaseAction
import com.quickref.plugin.download.DownloadManager
import com.quickref.plugin.download.DownloadResult
import com.quickref.plugin.download.DownloadTask
import com.quickref.plugin.extension.diff
import com.quickref.plugin.extension.isAndroidFrameworkClass
import com.quickref.plugin.extension.packageName
import com.quickref.plugin.extension.pathname
import com.quickref.plugin.extension.toFileRef
import com.quickref.plugin.version.AndroidVersion
import com.quickref.plugin.widget.AndroidVersionsPopView
import java.io.File

class DiffSourceAction : BaseAction() {

    override fun update(e: AnActionEvent) {
        val hasElement = e.getData(LangDataKeys.PSI_ELEMENT) != null
        e.presentation.isVisible = hasElement && e.project != null && e.packageName()?.isAndroidFrameworkClass() == true
    }

    override fun actionPerformed(e: AnActionEvent) {
        val element = e.getData(LangDataKeys.PSI_ELEMENT) ?: return
        val fileRef = element.pathname().toFileRef()
        val path = "$fileRef.${element.containingFile.fileType.defaultExtension}"
        val project = e.project ?: return

        AndroidVersionsPopView(e)
            .show("Choose First Version", AndroidVersion.merged) { _, firstVersion ->
                PluginLogger.debug("diff action firstVersion=$firstVersion")

                AndroidVersionsPopView(e)
                    .show("Choose Second Version", AndroidVersion.merged) { _, secondVersion ->

                        PluginLogger.debug("diff action secondVersion=$secondVersion")

                        val title = "Diff：$firstVersion-$secondVersion"

                        val task = object : Task.Backgroundable(project, title) {
                            override fun run(progressIndicator: ProgressIndicator) {
                                val task1 = DownloadTask(path, firstVersion)
                                val task2 = DownloadTask(path, secondVersion)
                                DownloadManager.downloadFile(
                                    progressIndicator,
                                    arrayOf(task1, task2),
                                    object : DownloadResult {
                                        override fun onSuccess(output: HashMap<String, File>) {
                                            val files = output.values.toMutableList()
                                            PluginLogger.debug("DownloadResult=$files")
                                            if (files.size < 2) {
                                                Notifier.errorNotification(project, "Error: Download $task1")
                                                return
                                            }
                                            project.diff(files[0], files[1])
                                        }

                                        override fun onFailure(msg: String, throwable: Throwable?) {
                                            Notifier.errorNotification(project, "Error:$msg")
                                        }
                                    }, true
                                )
                            }
                        }

                        ProgressManager.getInstance().run(task)
                    }

            }
    }
}
