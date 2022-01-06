package com.quickref.plugin

import com.intellij.notification.Notification
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.notification.NotificationsConfiguration
import com.intellij.openapi.diagnostic.Logger

object PluginLogger {

    private const val TAG = App.AppTitle

    private const val IS_DEBUG = true

    private val LOG = Logger.getInstance(PluginLogger::class.java)

    init {
        NotificationsConfiguration.getNotificationsConfiguration()
            .register(TAG, NotificationDisplayType.NONE)
    }

    fun debug(text: String?) {
        if (IS_DEBUG) {
            print(NotificationType.INFORMATION, text)
            LOG.debug(text)
        }
    }

    fun warn(text: String?) {
        if (IS_DEBUG) {
            print(NotificationType.WARNING, text)
            LOG.warn(text)
        }
    }

    fun error(text: String?) {
        print(NotificationType.ERROR, text)
        LOG.error(text)
    }

    fun e(throwable: Throwable) {
        error(throwable.message)
    }

    private fun print(type: NotificationType, msg: String?) {
        val ste = Throwable().stackTrace[2]
        val prefix = ste.fileName
        val lineNum = ste.lineNumber
        val text = "[$prefix:$lineNum] $msg"
        Notifications.Bus.notify(Notification(TAG, TAG, text, type))
    }
}
