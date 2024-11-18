package com.quickref.plugin.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.quickref.plugin.App
import com.quickref.plugin.Notifier
import com.quickref.plugin.extension.guessFileName
import com.quickref.plugin.extension.isAndroidClass
import com.quickref.plugin.extension.pathname
import com.quickref.plugin.version.AndroidVersion
import com.quickref.plugin.viewer.CodeSearchViewer
import com.quickref.plugin.widget.AndroidVersionsPopView

/**
 * more apis @see <a href="https://developers.google.com/code-search/reference">Android Code Search.</a>
 *
 * will open browser with the url like these:
 *
 * https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/java/android/app/Activity.java
 * https://cs.android.com/android/platform/superproject/+/android-10.0.0_r1:frameworks/base/core/java/android/app/Activity.java
 */
class AndroidCodeSearchAction : BaseAction() {

    override fun update(e: AnActionEvent) {
        val psiElement = e.getData(LangDataKeys.PSI_ELEMENT)
        e.presentation.isVisible = psiElement != null && psiElement.pathname().isAndroidClass()
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun actionPerformed(e: AnActionEvent) {
        val fileName = e.guessFileName()

        val javaFileMapping = App.db.javaFileMappingQueries.getJavaFile(
            fileName, 1
        ).executeAsOneOrNull()

        if (javaFileMapping == null) {
            Notifier.errorNotification(e.project, "Can't find the file mapping for $fileName")
            return
        }

        val javaPath = javaFileMapping.path
        val minVersion = javaFileMapping.version?.toInt() ?: 1

        AndroidVersionsPopView(
            project = e.project,
            dataContext = e.dataContext
        )
            .show(
                "Choose $fileName Version",
                AndroidVersion.mergedDownloadableSource(miniVersion = minVersion)
            ) { _, version ->
                if (javaPath.isEmpty()) {
                    CodeSearchViewer.open(version, fileName)
                } else {
                    CodeSearchViewer.open(version, javaPath)
                }
            }
    }
}
