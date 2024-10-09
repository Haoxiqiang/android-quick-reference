package com.quickref.plugin.extension

import com.intellij.diff.DiffManager
import com.intellij.diff.contents.FileDocumentContentImpl
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.quickref.plugin.App
import com.quickref.plugin.Notifier
import com.quickref.plugin.PluginLogger
import com.quickref.plugin.download.DownloadManager
import com.quickref.plugin.download.DownloadResult
import com.quickref.plugin.download.DownloadTask
import java.io.File

fun Project.downloadFile(
    tasks: Array<DownloadTask>,
    success: (output: HashMap<String, File>) -> Unit,
    failed: (String, Throwable?) -> Unit
) {
    val title = "Download：${tasks.joinToString { task -> "${task.versionName}-${task.path}" }}"
    val task = object : Task.Backgroundable(this, title) {
        override fun run(progressIndicator: ProgressIndicator) {
            DownloadManager.downloadFile(
                progressIndicator,
                tasks,
                object : DownloadResult {
                    override fun onSuccess(output: HashMap<String, File>) {
                        success.invoke(output)
                    }

                    override fun onFailure(msg: String, throwable: Throwable?) {
                        Notifier.errorNotification(project, "Error:$msg")
                        failed.invoke(msg, throwable)
                    }
                }, true
            )
        }
    }
    ProgressManager.getInstance().run(task)
}

fun Project.openFileInEditor(file: File, line: Int = -1) {
    if (file.isDirectory) {
        PluginLogger.error("can't open directory with the editor.${file.absolutePath}")
        return
    }

    PluginLogger.debug("openFileInEditor:${file.absolutePath}#$line")

    ApplicationManager.getApplication().invokeLater {
        val lFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)
        if (lFile != null && lFile.isValid) {
            val descriptor = if (line > 0) {
                OpenFileDescriptor(this, lFile, line, -1)
            } else {
                OpenFileDescriptor(this, lFile)
            }
            descriptor.isUseCurrentWindow = true
            descriptor.navigateInEditor(this, true)
        }
    }
}

fun Project.diff(file1: File, file2: File) {
    ApplicationManager.getApplication().invokeLater {
        try {
            val v1 = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file1)
            val document1 = FileDocumentManager.getInstance().getDocument(v1!!)
            val fileDocumentContent1 = FileDocumentContentImpl(this, document1!!, v1)
            val v2 = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file2)
            val document2 = FileDocumentManager.getInstance().getDocument(v2!!)
            val fileDocumentContent2 = FileDocumentContentImpl(this, document2!!, v2)
            val simpleDiffRequest = SimpleDiffRequest(
                App.AppTitle, fileDocumentContent1, fileDocumentContent2,
                file1.name, file2.name
            )
            DiffManager.getInstance().showDiff(this, simpleDiffRequest)
        } catch (e: Exception) {
            Notifier.errorNotification(this, "Diff Source Error:" + e.message)
        }
    }
}
