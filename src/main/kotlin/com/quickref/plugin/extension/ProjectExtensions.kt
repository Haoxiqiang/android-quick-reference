package com.quickref.plugin.extension

import com.intellij.diff.DiffManager
import com.intellij.diff.contents.FileDocumentContentImpl
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.fileEditor.ex.FileEditorProviderManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.quickref.plugin.App
import com.quickref.plugin.Notifier
import com.quickref.plugin.PluginLogger
import java.io.File

fun Project.openFileInEditor(file: File) {
    if (file.isDirectory) {
        PluginLogger.error("can't open directory with the editor.${file.absolutePath}")
        return
    }
    ApplicationManager.getApplication().invokeLater {
        val lFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)
        if (lFile != null && lFile.isValid) {

            lFile.isWritable = false

            val providers = FileEditorProviderManager.getInstance().getProviders(this, lFile)
            if (providers.isNotEmpty()) {
                val descriptor = OpenFileDescriptor(this, lFile)
                descriptor.isUseCurrentWindow = true
                descriptor.navigateInEditor(this, true)
            }
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
