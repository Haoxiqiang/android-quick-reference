@file:Suppress("DEPRECATION")

package com.quickref.plugin.notification

import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.quickref.plugin.App

class NotificationDefault : Notification {

    override fun showNotification(
        project: Project?,
        message: String,
        type: Notification.Type
    ) {
        NotificationGroup
            .balloonGroup(App.notificationGroup)
            .createNotification(
                App.AppTitle, message, when (type) {
                    Notification.Type.ERROR -> NotificationType.ERROR
                    else -> NotificationType.INFORMATION
                }, null
            )
            .notify(project)
    }
}
