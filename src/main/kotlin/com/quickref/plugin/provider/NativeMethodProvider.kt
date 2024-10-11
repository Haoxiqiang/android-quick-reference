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
import com.quickref.plugin.download.DownloadManager
import com.quickref.plugin.download.DownloadResult
import com.quickref.plugin.download.DownloadTask
import com.quickref.plugin.extension.openFileInEditor
import com.quickref.plugin.extension.pathname
import com.quickref.plugin.version.AndroidVersion
import com.quickref.plugin.widget.AndroidVersionsPopView
import java.awt.event.MouseEvent
import java.io.File

// support jni jump.
class NativeMethodProvider : LineMarkerProvider, GutterIconNavigationHandler<PsiMethod> {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiMethod>? {
        return if (element is PsiMethod
            && element.modifierList.hasExplicitModifier(PsiModifier.NATIVE)
        ) {
            // add icon to all framework native method
            val toolTip = Function<PsiMethod, String> { "Show Native implication by the file mapping." }
            val navHandler: GutterIconNavigationHandler<PsiMethod> = this@NativeMethodProvider

            val jniClass = element.pathname()?.replace('.', '/') ?: return null
            App.db.nativeFileMappingQueries.getNativeFile(
                clazz = jniClass,
                version = 1
            ).executeAsOneOrNull() ?: return null

            // Add the property to a collection of line marker info
            LineMarkerInfo(
                /* element = */ element,
                /* range = */ element.textRange,
                /* icon = */ ImageAssets.NATIVE,
                /* tooltipProvider = */ toolTip,
                /* navHandler = */ navHandler,
                /* alignment = */ GutterIconRenderer.Alignment.LEFT
            )
            /* accessibleNameProvider = */ { "Show Native implication by the file mapping." }
        } else {
            null
        }
    }

    override fun navigate(e: MouseEvent, elt: PsiMethod) {

        val dataContext = DataManager.getInstance().getDataContext(e.component)
        val presentation = Presentation()
        val anActionEvent = AnActionEvent
            .createFromInputEvent(e, "", presentation, dataContext, true, true)

        val jniClass = elt.pathname()?.replace('.', '/')
        if (jniClass == null) {
            Notifier.errorNotification(elt.project, "Error: Can't find the class path.")
            return
        }

        // try check the version
        val nativeFile = App.db.nativeFileMappingQueries.getNativeFile(
            clazz = jniClass,
            version = 1
        ).executeAsOneOrNull()
        if (nativeFile == null) {
            Notifier.errorNotification(elt.project, "Error: Can't find the native file.")
            return
        } else {
            val miniVersion = nativeFile.version?.toInt() ?: 1
            val filePath = nativeFile.path
            val fileName = elt.containingFile.name.replace(".java", ".cpp")
            val methodName = elt.name

            AndroidVersionsPopView(anActionEvent)
                .show(
                    "Choose Version",
                    AndroidVersion.mergedDownloadableSource(miniVersion = miniVersion)
                ) { _, version ->
                    PluginLogger.debug("down raw native file[v:$version:$version]:$jniClass#$methodName-$filePath")

                    // try to get all extensions
                    val project = elt.project
                    val title = "Download：$version-$fileName"

                    val task = object : Task.Backgroundable(project, title) {
                        override fun run(progressIndicator: ProgressIndicator) {
                            val nativeFileTask = DownloadTask(path = filePath, versionName = version)
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
                                        val file = files[0]
                                        if (!file.exists()) {
                                            Notifier.errorNotification(project, "Error: Download $fileName")
                                            return
                                        }

                                        // TODO find line in the file. get native method line.
                                        // try guess line number.
                                        file.readLines().forEachIndexed { index, line ->
                                            if (line.contains(methodName)) {
                                                project.openFileInEditor(file, index)
                                                return
                                            }
                                        }
                                        project.openFileInEditor(file, 0)
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
}
