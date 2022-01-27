package com.quickref.plugin.provider

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier
import com.intellij.util.Function
import com.quickref.plugin.App
import com.quickref.plugin.ImageAssets
import com.quickref.plugin.Notifier
import com.quickref.plugin.PluginLogger
import com.quickref.plugin.db.NativeMethodMapping
import com.quickref.plugin.download.DownloadManager
import com.quickref.plugin.download.DownloadResult
import com.quickref.plugin.download.DownloadTask
import com.quickref.plugin.extension.isAndroidFrameworkClass
import com.quickref.plugin.extension.openFileInEditor
import com.quickref.plugin.version.AndroidVersion
import com.quickref.plugin.widget.AndroidVersionsPopView
import java.awt.event.MouseEvent
import java.io.File

// support jni jump.
class NativeMethodProvider : LineMarkerProvider, GutterIconNavigationHandler<PsiMethod> {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiMethod>? {
        return if (element is PsiMethod
            && element.modifierList.hasExplicitModifier(PsiModifier.NATIVE)
            && element.isAndroidFrameworkClass()
        ) {
            // add icon to all framework native method
            val toolTip = Function<PsiMethod, String> { "Show Native implication by the file mapping." }
            val navHandler: GutterIconNavigationHandler<PsiMethod> = this@NativeMethodProvider

            // Add the property to a collection of line marker info
            LineMarkerInfo(
                element,
                element.textRange,
                ImageAssets.NATIVE,
                toolTip,
                navHandler,
                GutterIconRenderer.Alignment.LEFT,
            )
        } else {
            null
        }
    }

    override fun navigate(e: MouseEvent, elt: PsiMethod) {

        val dataContext = DataManager.getInstance().getDataContext(e.component)
        val presentation = Presentation()
        val anActionEvent = AnActionEvent
            .createFromInputEvent(e, "", presentation, dataContext, true, true)

        AndroidVersionsPopView(anActionEvent)
            .show("Choose Version", AndroidVersion.sourceDownloadableVersions) { _, version ->

                val fileName = elt.containingFile.name.replace(".java", ".cpp")
                val methodName = elt.name
                val versionNumber = AndroidVersion.getBuildNumber(version).toLong()

                PluginLogger.debug("down raw native file[v:$version:$versionNumber]:$fileName#$methodName")

                // try get all extensions
                val project = elt.project
                val title = "Download：$version-$fileName"

                val task = object : Task.Backgroundable(project, title) {
                    override fun run(progressIndicator: ProgressIndicator) {
                        val nativeFileTask = DownloadTask(path = fileName, versionName = version)
                        DownloadManager.downloadFile(
                            progressIndicator,
                            downloadTasks = arrayOf(nativeFileTask),
                            result = object : DownloadResult {
                                override fun onSuccess(output: HashMap<String, File>) {
                                    val files = output.values.toMutableList()
                                    if (files.isEmpty()) {
                                        Notifier.errorNotification(project, "Error: Download $fileName")
                                        return
                                    }
                                    // get native method line.
                                    val nativeMethod: NativeMethodMapping? =
                                        App.db.nativeMethodMappingQueries.getNativeMethod(
                                            key = fileName,
                                            method = methodName,
                                            version = versionNumber
                                        ).executeAsOneOrNull()

                                    val lineNumber =
                                        if (nativeMethod?.defLine != null && nativeMethod.defLine > 0) {
                                            nativeMethod.defLine
                                        } else if (nativeMethod?.jniLine != null && nativeMethod.jniLine > 0) {
                                            nativeMethod.jniLine
                                        } else {
                                            // TODO find line in the file.
                                            0L
                                        }

                                    project.openFileInEditor(files[0], lineNumber.toInt())
                                }

                                override fun onFailure(msg: String, throwable: Throwable?) {
                                    Notifier.errorNotification(project, "Error:$msg")
                                }
                            },
                            isSync = true
                        )
                    }
                }

                val progressIndicator =
                    ProgressManager.getInstance().progressIndicator ?: BackgroundableProcessIndicator(task)
                progressIndicator.isIndeterminate = true
                ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, progressIndicator)
            }
    }
}
