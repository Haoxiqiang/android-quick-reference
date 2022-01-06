package com.quickref.plugin.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.quickref.plugin.action.base.BaseAction
import com.quickref.plugin.extension.isAndroidFrameworkClass
import com.quickref.plugin.extension.packageName
import com.quickref.plugin.extension.pathname
import com.quickref.plugin.extension.toFileRef
import com.quickref.plugin.version.AndroidVersion
import com.quickref.plugin.viewer.CodeSearchViewer
import com.quickref.plugin.widget.AndroidVersionsPopView
import java.io.File

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
        val hasElement = e.getData(LangDataKeys.PSI_ELEMENT) != null
        e.presentation.isVisible = hasElement && e.packageName()?.isAndroidFrameworkClass() == true
    }

    override fun actionPerformed(e: AnActionEvent) {
        val element = e.getData(LangDataKeys.PSI_ELEMENT) ?: return
        val fileRef = element.pathname().toFileRef()
        val path = "$fileRef.${element.containingFile.fileType.defaultExtension}"
        val file = File(path)
        AndroidVersionsPopView(e)
            .show("Choose ${file.name} Version", AndroidVersion.merged) { _, version ->
                CodeSearchViewer.open(version, path)
            }
    }
}
