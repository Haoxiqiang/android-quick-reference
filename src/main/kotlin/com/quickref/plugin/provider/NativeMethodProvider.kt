package com.quickref.plugin.provider

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
        val lineMarkerInfo =
            if (element is PsiMethod
                && element.modifierList.hasExplicitModifier(PsiModifier.NATIVE)
                && element.isAndroidFrameworkClass()
            ) {
                // add icon to all framework native method
                val toolTip = Function<PsiMethod, String> { "Show Native implication by the file mapping." }
                val supplier = java.util.function.Supplier<String> { "" }
                val navHandler: GutterIconNavigationHandler<PsiMethod> = this@NativeMethodProvider
                LineMarkerInfo(
                    element,
                    element.textRange,
                    ImageAssets.NATIVE,
                    toolTip,
                    navHandler,
                    GutterIconRenderer.Alignment.LEFT,
                    supplier
                )
            } else {
                null
            }
        return lineMarkerInfo
    }

    override fun navigate(e: MouseEvent, elt: PsiMethod) {
        val anActionEvent = AnActionEvent.createFromInputEvent(e, "", Presentation(), DataContext { key ->
            if (CommonDataKeys.PROJECT.name == key) {
                return@DataContext elt.project
            } else if (CommonDataKeys.PSI_ELEMENT.name == key) {
                return@DataContext elt
            }
            null
        })

        AndroidVersionsPopView(anActionEvent)
            .show("Choose Version", AndroidVersion.merged) { _, version ->

                val fileName = elt.containingFile.name.replace(".java", ".cpp")
                val methodName = elt.name
                val versionNumber = AndroidVersion.getBuildNumber(version).toLong()

                PluginLogger.debug("down raw native file[v:$version:$versionNumber]:$fileName#$methodName")

                // try get all extensions
                val project = elt.project
                val title = "Download：$version-$fileName"

                val task = object : Task.Backgroundable(project, title) {
                    override fun run(progressIndicator: ProgressIndicator) {
                        val nativeFileTask = DownloadTask(fileName, version)
                        DownloadManager.downloadFile(
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
                            isSync = true,
                            sameTarget = true
                        )
                    }
                }

                ProgressManager.getInstance().run(task)
            }
    }
}
