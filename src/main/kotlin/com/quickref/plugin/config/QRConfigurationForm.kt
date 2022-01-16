package com.quickref.plugin.config

import com.intellij.ui.IdeBorderFactory
import com.intellij.util.ui.FormBuilder
import com.quickref.plugin.config.QuickReferenceConfigStorage.Companion.instance
import javax.swing.JComponent
import javax.swing.JPanel

class QRConfigurationForm {

    private val quickReferenceConfigStorage: QuickReferenceConfigStorage = instance()

    private var myMainPanel: JPanel = FormBuilder.createFormBuilder()

        .addComponent(QRSearchMenu.container)
        .addComponent(QRCacheMenu.container)
        .addComponent(QRVersionsMenu.container)

        .panel.apply {
            border = IdeBorderFactory.createTitledBorder("Quick Reference Settings")
        }

    fun createPanel(): JComponent {
        return myMainPanel
    }

    fun apply() {
        QRSearchMenu.applyState(quickReferenceConfigStorage)
        QRCacheMenu.applyState(quickReferenceConfigStorage)
        QRVersionsMenu.applyState(quickReferenceConfigStorage)

    }

    fun reset() {
        QRSearchMenu.loadState(quickReferenceConfigStorage)
        QRCacheMenu.loadState(quickReferenceConfigStorage)
        QRVersionsMenu.loadState(quickReferenceConfigStorage)
    }

    val isNotModified: Boolean
        get() = (QRSearchMenu.checkNotModified(quickReferenceConfigStorage)
            && QRVersionsMenu.checkNotModified(quickReferenceConfigStorage)
            && QRCacheMenu.checkNotModified(quickReferenceConfigStorage)
            )
}
