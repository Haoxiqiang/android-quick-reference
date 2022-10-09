package com.quickref.plugin

import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.project.Project
import com.quickref.plugin.notification.Notification
import com.quickref.plugin.notification.NotificationDefault
import com.quickref.plugin.notification.NotificationEmpty

object Notifier {
    private const val minVersion = 190

    private val notification by lazy {
        val apiComponent = ApplicationInfo.getInstance().build.components[0]
        if (apiComponent < minVersion) {
            NotificationEmpty()
        } else {
            NotificationDefault()
        }
    }

    fun errorNotification(project: Project?, message: String?) {
        val content = if (message == null || message.trim { it <= ' ' }.isEmpty()) "[Empty]" else message
        notification.showNotification(project, content, Notification.Type.ERROR)
    }

    fun infoNotification(project: Project?, message: String?) {
        val content = if (message == null || message.trim { it <= ' ' }.isEmpty()) "[Empty]" else message
        notification.showNotification(project, content, Notification.Type.INFORMATION)
    }

}
