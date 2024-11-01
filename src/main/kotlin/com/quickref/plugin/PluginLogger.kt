package com.quickref.plugin

import com.intellij.openapi.diagnostic.Logger

object PluginLogger {

    private const val IS_DEBUG = true

    private val LOG = Logger.getInstance(PluginLogger::class.java)

    fun debug(text: String?) {
        if (IS_DEBUG) {
            LOG.debug(text)
        }
    }

    fun warn(text: String?) {
        if (IS_DEBUG) {
            LOG.warn(text)
        }
    }

    fun error(text: String?) {
        LOG.error(text)
    }

    fun e(throwable: Throwable) {
        error(throwable.message)
    }
}
