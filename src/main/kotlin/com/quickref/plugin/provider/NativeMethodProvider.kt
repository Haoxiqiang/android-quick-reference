package com.quickref.plugin.provider

import com.intellij.codeHighlighting.Pass
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier
import com.quickref.plugin.ImageAssets
import com.quickref.plugin.Notifier
import com.quickref.plugin.PluginLogger
import com.quickref.plugin.download.DownloadManager
import com.quickref.plugin.download.DownloadResult
import com.quickref.plugin.download.DownloadTask
import com.quickref.plugin.extension.isAndroidFrameworkClass
import com.quickref.plugin.extension.openFileInEditor
import com.quickref.plugin.extension.pathname
import com.quickref.plugin.extension.toFileRef
import com.quickref.plugin.version.AndroidVersion
import com.quickref.plugin.widget.AndroidVersionsPopView
import java.awt.event.MouseEvent
import java.io.File

// support jni jump.
class NativeMethodProvider : LineMarkerProvider, GutterIconNavigationHandler<PsiElement> {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiElement>? {
        if (element is PsiMethod) {
            // 只展示系统 native 方法
            if (element.modifierList.hasExplicitModifier(PsiModifier.NATIVE)) {
                // add icon to all framework native method
                if (element.isAndroidFrameworkClass()) {
                    @Suppress("DEPRECATION")
                    return LineMarkerInfo(
                        element,
                        element.textRange,
                        ImageAssets.NATIVE,
                        Pass.LINE_MARKERS,
                        null,
                        this@NativeMethodProvider,
                        GutterIconRenderer.Alignment.LEFT,
                    )
                }
            }
        }
        return null
    }

    override fun collectSlowLineMarkers(
        elements: MutableList<out PsiElement>,
        result: MutableCollection<in LineMarkerInfo<*>>
    ) {

    }

    override fun navigate(e: MouseEvent, element: PsiElement) {
        val anActionEvent = AnActionEvent.createFromInputEvent(e, "", Presentation(), DataContext { key ->
            if (CommonDataKeys.PROJECT.name == key) {
                return@DataContext element.project
            } else if (CommonDataKeys.PSI_ELEMENT.name == key) {
                return@DataContext element
            }
            null
        })

        val fileRef = element.pathname().toFileRef()

        AndroidVersionsPopView(anActionEvent)
            .show("Choose Version", AndroidVersion.merged) { _, version ->

                PluginLogger.debug("down raw file from androidxref.com version=$version fileRef=$fileRef")

                // try get all extensions
                val path1 = "$fileRef.cpp"
                val path2 = "$fileRef.c"
                val path3 = "$fileRef.cc"

                val project = element.project

                val title = "Download：$version-[.cpp,.c,.cc]"

                val task = object : Task.Backgroundable(project, title) {
                    override fun run(progressIndicator: ProgressIndicator) {
                        val task1 = DownloadTask(path1, version)
                        val task2 = DownloadTask(path2, version)
                        val task3 = DownloadTask(path3, version)
                        DownloadManager.downloadFile(
                            downloadTasks = arrayOf(task1, task2, task3),
                            result = object : DownloadResult {
                                override fun onSuccess(output: HashMap<String, File>) {
                                    val files = output.values.toMutableList()
                                    if (files.isEmpty()) {
                                        Notifier.errorNotification(project, "Error: Download $fileRef")
                                        return
                                    }
                                    project.openFileInEditor(files[0])
                                }

                                override fun onFailure(msg: String, throwable: Throwable?) {
                                    Notifier.errorNotification(project, "Error:$msg")
                                }
                            },
                            isSync = true,
                            sameTarget = true
                        )
                        progressIndicator.fraction = 0.5
                    }
                }

                ProgressManager.getInstance().run(task)
            }
    }
}
