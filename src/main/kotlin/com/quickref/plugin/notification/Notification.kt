package com.quickref.plugin.notification

import com.intellij.openapi.project.Project

interface Notification {

    enum class Type {
        INFORMATION,
        WARNING,
        ERROR
    }

    fun showNotification(
        project: Project?,
        message: String,
        type: Type = Type.INFORMATION
    )
}
