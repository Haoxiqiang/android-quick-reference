package com.quickref.plugin.notification

import com.intellij.openapi.project.Project

class NotificationEmpty : Notification {

    override fun showNotification(
        project: Project?,
        message: String,
        type: Notification.Type
    ) {

    }
}
