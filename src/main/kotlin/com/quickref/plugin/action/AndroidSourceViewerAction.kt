package com.quickref.plugin.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.ui.showOkNoDialog
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiImportStatement
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiVariable
import com.quickref.plugin.App
import com.quickref.plugin.Notifier
import com.quickref.plugin.action.base.BaseAction
import com.quickref.plugin.download.DownloadManager
import com.quickref.plugin.download.DownloadResult
import com.quickref.plugin.entity.DownloadTask
import com.quickref.plugin.extension.isAndroidFrameworkClass
import com.quickref.plugin.extension.openFileInEditor
import com.quickref.plugin.extension.packageName
import com.quickref.plugin.extension.pathname
import com.quickref.plugin.extension.toFileRef
import com.quickref.plugin.version.AndroidVersion
import com.quickref.plugin.viewer.CodeSearchViewer
import com.quickref.plugin.widget.AndroidVersionsPopView
import java.io.File

class AndroidSourceViewerAction : BaseAction() {

    override fun update(e: AnActionEvent) {
        val hasElement = e.getData(LangDataKeys.PSI_ELEMENT) != null
        e.presentation.isVisible = hasElement && e.project != null && e.packageName()?.isAndroidFrameworkClass() == true
    }

    override fun actionPerformed(e: AnActionEvent) {
        val element = e.getData(LangDataKeys.PSI_ELEMENT) ?: return

        val name = element.pathname()

        if (name.isNullOrEmpty()) {
            return
        }

        // if var element, try to fill the package name.
        val varPackageName = tryGetVarPackageName(e, element, name)

        val target = varPackageName.ifEmpty {
            name
        }

        val fileRef = target.toFileRef()
        val extension = if (element.containingFile is PsiJavaFile) {
            "java"
        } else {
            element.containingFile.fileType.defaultExtension
        }
        val path = "$fileRef.$extension"
        val file = File(path)

        val project = e.project ?: return

        AndroidVersionsPopView(e)
            .show("Choose ${file.name} Version", AndroidVersion.merged) { _, version ->

                val title = "Download：$version-$path"

                val task = object : Task.Backgroundable(project, title) {
                    override fun run(progressIndicator: ProgressIndicator) {

                        val task = DownloadTask(path, version)
                        DownloadManager.downloadFile(
                            arrayOf(task),
                            object : DownloadResult {
                                override fun onSuccess(output: HashMap<String, File>) {
                                    val downloadFile = output[task.key()]
                                    if (downloadFile == null) {
                                        Notifier.errorNotification(project, "Error: Download $task")
                                        return
                                    }
                                    project.openFileInEditor(downloadFile)
                                }

                                override fun onFailure(msg: String, throwable: Throwable?) {
                                    Notifier.errorNotification(project, "Error:$msg")
                                    // all download failed, use cs search.
                                    if (showOkNoDialog(
                                            title = App.AppTitle,
                                            message = "Download source failed. Go to CodeSearch?",
                                            project = project
                                        )
                                    ) {
                                        CodeSearchViewer.search(path)
                                    }
                                }
                            }, true
                        )
                        progressIndicator.fraction = 0.5
                    }
                }

                ProgressManager.getInstance().run(task)
            }
    }

    private fun tryGetVarPackageName(e: AnActionEvent, element: PsiElement, name: String): String {
        if (element is PsiVariable) {
            // origin file import analysis.
            val psiFile = e.getData(LangDataKeys.PSI_FILE)
            if (psiFile is PsiJavaFile) {
                val imports =
                    psiFile.importList?.children?.filterIsInstance<PsiImportStatement>()?.filter { psiImportStatement ->
                        psiImportStatement.qualifiedName == name
                    }
                // in the list use the import
                return "${
                    if (imports?.isNotEmpty() == true) {
                        imports[0].qualifiedName
                    } else {
                        // use the parent package
                        psiFile.packageName
                    }
                }.$name"
            }
        }
        return ""
    }
}
