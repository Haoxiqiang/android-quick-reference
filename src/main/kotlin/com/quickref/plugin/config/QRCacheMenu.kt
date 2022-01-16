package com.quickref.plugin.config

import com.intellij.ide.plugins.newui.HorizontalLayout
import com.intellij.openapi.application.ApplicationManager
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.TitledSeparator
import com.intellij.util.ui.FormBuilder
import com.quickref.plugin.App
import org.apache.commons.io.FileUtils
import java.io.IOException
import java.util.Locale
import javax.swing.JButton
import javax.swing.JLabel

internal object QRCacheMenu {

    private var cleanCache: JButton = JButton("Clean Cache")
        .apply {
            addActionListener {
                try {
                    FileUtils.cleanDirectory(App.CACHE_DIR)
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
                calCacheSize()
            }
        }
    private var cacheSize: JLabel = JLabel("Currently Storage ...")

    val container = FormBuilder.createFormBuilder()
        .addComponent(TitledSeparator("Manager"))
        .addComponent(FormBuilder.createFormBuilder()
            .setFormLeftIndent(IdeBorderFactory.TITLED_BORDER_INDENT)
            .addComponent(cacheSize)
            .addComponent(cleanCache)
            .panel.apply {
                layout = HorizontalLayout(IdeBorderFactory.TITLED_BORDER_INDENT)
                calCacheSize()
            })
        .setVerticalGap(IdeBorderFactory.TITLED_BORDER_INDENT)
        .panel

    private fun calCacheSize() {
        ApplicationManager.getApplication().invokeLater {
            val size = FileUtils.sizeOfDirectory(App.CACHE_DIR)
            val fileSizeDisplay = FileUtils.byteCountToDisplaySize(size)
            cacheSize.text = String.format(Locale.ENGLISH, "Currently Storage    %s", fileSizeDisplay)
        }
    }

    @Suppress("UnusedPrivateMember")
    fun applyState(quickReferenceConfigStorage: QuickReferenceConfigStorage) {
        calCacheSize()
    }

    @Suppress("UnusedPrivateMember")
    fun loadState(quickReferenceConfigStorage: QuickReferenceConfigStorage) {

    }

    @Suppress("UnusedPrivateMember", "FunctionOnlyReturningConstant")
    fun checkNotModified(quickReferenceConfigStorage: QuickReferenceConfigStorage): Boolean {
        return true
    }

}
