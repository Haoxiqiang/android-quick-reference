package com.quickref.plugin.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.quickref.plugin.App
import com.quickref.plugin.extension.guessFileName
import com.quickref.plugin.extension.isAndroidFrameworkClass
import com.quickref.plugin.extension.packageName
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
        e.presentation.isVisible = psiElement != null && e.packageName()?.isAndroidFrameworkClass() == true
    }

    override fun actionPerformed(e: AnActionEvent) {
        val fileName = e.guessFileName()
        AndroidVersionsPopView(e)
            .show("Choose $fileName Version", AndroidVersion.sourceSearchableVersions) { _, version ->
                val versionNumber = AndroidVersion.getBuildNumber(version).toLong()
                val javaPath = App.db.javaFileMappingQueries.getJavaFile(
                    file = fileName, versionNumber
                ).executeAsOneOrNull()
                if (javaPath.isNullOrEmpty()) {
                    CodeSearchViewer.open(version, fileName)
                } else {
                    CodeSearchViewer.open(version, javaPath)
                }
            }
    }
}
