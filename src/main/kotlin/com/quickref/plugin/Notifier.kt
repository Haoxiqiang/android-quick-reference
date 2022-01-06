package com.quickref.plugin

import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

object Notifier {

    private fun getNotification(): NotificationGroup {
        return NotificationGroupManager.getInstance().getNotificationGroup(App.notificationGroup)
    }

    private fun showNotification(
        project: Project?,
        message: String?,
        notificationType: NotificationType = NotificationType.INFORMATION
    ) {
        val content = if (message == null || message.trim { it <= ' ' }.isEmpty()) "[Empty]" else message

        getNotification()
            .createNotification(
                title = App.AppTitle,
                content = content,
                type = notificationType,
                listener = null
            )
            .notify(project)
    }

    fun errorNotification(project: Project?, message: String?) {
        showNotification(project, message, NotificationType.ERROR)
    }

    fun infoNotification(project: Project?, message: String?) {
        showNotification(project, message, NotificationType.INFORMATION)
    }
}
