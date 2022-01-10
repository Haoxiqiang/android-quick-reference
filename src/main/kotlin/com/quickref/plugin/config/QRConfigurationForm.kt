package com.quickref.plugin.config

import com.intellij.ide.plugins.newui.HorizontalLayout
import com.intellij.openapi.application.ApplicationManager
import com.intellij.ui.IdeBorderFactory
import com.intellij.util.ui.FormBuilder
import com.quickref.plugin.App.CACHE_DIR
import com.quickref.plugin.config.QuickReferenceConfigStorage.Companion.instance
import org.apache.commons.io.FileUtils
import java.io.IOException
import java.util.Locale
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class QRConfigurationForm {

    private val quickReferenceConfigStorage: QuickReferenceConfigStorage = instance()

    private var enableGoogleSearch: JCheckBox = JCheckBox("Enable Google Search")
    private var enableBingSearch: JCheckBox = JCheckBox("Enable Bing Search")
    private var enableStackOverflow: JCheckBox = JCheckBox("Enable StackOverflow Search")
    private var enableGithubSearch: JCheckBox = JCheckBox("Enable Github Search")
    private var enableCodeSearch: JCheckBox = JCheckBox("Enable Code Search")

    private var enableQuickSearch: JCheckBox = JCheckBox("Enable Quick Search")
        .apply {
            addChangeListener {
                val enabled = this.isSelected
                enableGoogleSearch.isEnabled = enabled
                enableBingSearch.isEnabled = enabled
                enableStackOverflow.isEnabled = enabled
                enableGithubSearch.isEnabled = enabled
                enableCodeSearch.isEnabled = enabled
            }
        }

    private var cleanCache: JButton = JButton("Clean Cache")
        .apply {
            addActionListener {
                try {
                    FileUtils.cleanDirectory(CACHE_DIR)
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
                calCacheSize()
            }
        }
    private var cacheSize: JLabel = JLabel("Cache Size ")

    private val cachePanel = FormBuilder.createFormBuilder()
        .addComponent(cacheSize)
        .addComponent(cleanCache)
        .panel.apply {
            layout = HorizontalLayout(IdeBorderFactory.TITLED_BORDER_INDENT)
        }

    private var myMainPanel: JPanel = FormBuilder.createFormBuilder()
        .addComponent(enableQuickSearch)

        .setFormLeftIndent(IdeBorderFactory.TITLED_BORDER_INDENT)
        .addComponent(enableGoogleSearch)
        .addComponent(enableBingSearch)
        .addComponent(enableStackOverflow)
        .addComponent(enableGithubSearch)
        .addComponent(enableCodeSearch)
        .setFormLeftIndent(IdeBorderFactory.TITLED_BORDER_LEFT_INSET)

        .addLabeledComponent(
            null,
            cachePanel,
            IdeBorderFactory.TITLED_BORDER_BOTTOM_INSET
        )
        .addComponentFillVertically(JPanel(), IdeBorderFactory.TITLED_BORDER_BOTTOM_INSET)

        .panel.apply {
            border = IdeBorderFactory.createTitledBorder("Quick Reference Settings")
        }


    fun createPanel(): JComponent {
        cacheSize.text = "..."
        calCacheSize()
        return myMainPanel
    }

    private fun calCacheSize() {
        ApplicationManager.getApplication().invokeLater {
            val size = FileUtils.sizeOfDirectory(CACHE_DIR)
            val fileSizeDisplay = FileUtils.byteCountToDisplaySize(size)
            cacheSize.text = String.format(Locale.ENGLISH, "Currently Storage    %.2fM", fileSizeDisplay)
        }
    }

    fun apply() {
        quickReferenceConfigStorage.enableQuickSearch = enableQuickSearch.isSelected
        quickReferenceConfigStorage.enableGoogleSearch = enableGoogleSearch.isSelected
        quickReferenceConfigStorage.enableBingSearch = enableBingSearch.isSelected
        quickReferenceConfigStorage.enableStackOverflow = enableStackOverflow.isSelected
        quickReferenceConfigStorage.enableGithubSearch = enableGithubSearch.isSelected
        quickReferenceConfigStorage.enableCodeSearch = enableCodeSearch.isSelected
    }

    fun reset() {
        enableQuickSearch.isSelected = quickReferenceConfigStorage.enableQuickSearch
        enableGoogleSearch.isSelected = quickReferenceConfigStorage.enableGoogleSearch
        enableBingSearch.isSelected = quickReferenceConfigStorage.enableBingSearch
        enableStackOverflow.isSelected = quickReferenceConfigStorage.enableStackOverflow
        enableGithubSearch.isSelected = quickReferenceConfigStorage.enableGithubSearch
        enableCodeSearch.isSelected = quickReferenceConfigStorage.enableCodeSearch
    }

    val isNotModified: Boolean
        get() = (quickReferenceConfigStorage.enableQuickSearch == enableQuickSearch.isSelected
            && quickReferenceConfigStorage.enableGoogleSearch == enableGoogleSearch.isSelected
            && quickReferenceConfigStorage.enableBingSearch == enableBingSearch.isSelected
            && quickReferenceConfigStorage.enableStackOverflow == enableStackOverflow.isSelected
            && quickReferenceConfigStorage.enableGithubSearch == enableGithubSearch.isSelected
            && quickReferenceConfigStorage.enableCodeSearch == enableCodeSearch.isSelected)
}
