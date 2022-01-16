package com.quickref.plugin.config

import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.TitledSeparator
import com.intellij.util.ui.FormBuilder
import javax.swing.JCheckBox
import javax.swing.JPanel

internal object QRSearchMenu {

    var enableGoogleSearch: JCheckBox = JCheckBox("Enable Google Search")
    var enableBingSearch: JCheckBox = JCheckBox("Enable Bing Search")
    var enableStackOverflow: JCheckBox = JCheckBox("Enable StackOverflow Search")
    var enableGithubSearch: JCheckBox = JCheckBox("Enable Github Search")
    var enableCodeSearch: JCheckBox = JCheckBox("Enable Code Search")


    var enableQuickSearch: JCheckBox = JCheckBox("Enable Quick Search")
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

    var container: JPanel = FormBuilder.createFormBuilder()
        .addComponent(TitledSeparator("Quick Search"))
        .addComponent(enableQuickSearch)
        .setFormLeftIndent(IdeBorderFactory.TITLED_BORDER_INDENT)
        .addComponent(enableGoogleSearch)
        .addComponent(enableBingSearch)
        .addComponent(enableStackOverflow)
        .addComponent(enableGithubSearch)
        .addComponent(enableCodeSearch)
        .setFormLeftIndent(IdeBorderFactory.TITLED_BORDER_LEFT_INSET)
        .panel

    fun applyState(quickReferenceConfigStorage: QuickReferenceConfigStorage) {
        quickReferenceConfigStorage.enableQuickSearch = enableQuickSearch.isSelected
        quickReferenceConfigStorage.enableGoogleSearch = enableGoogleSearch.isSelected
        quickReferenceConfigStorage.enableBingSearch = enableBingSearch.isSelected
        quickReferenceConfigStorage.enableStackOverflow = enableStackOverflow.isSelected
        quickReferenceConfigStorage.enableGithubSearch = enableGithubSearch.isSelected
        quickReferenceConfigStorage.enableCodeSearch = enableCodeSearch.isSelected
    }

    fun loadState(quickReferenceConfigStorage: QuickReferenceConfigStorage) {
        enableQuickSearch.isSelected = quickReferenceConfigStorage.enableQuickSearch
        enableGoogleSearch.isSelected = quickReferenceConfigStorage.enableGoogleSearch
        enableBingSearch.isSelected = quickReferenceConfigStorage.enableBingSearch
        enableStackOverflow.isSelected = quickReferenceConfigStorage.enableStackOverflow
        enableGithubSearch.isSelected = quickReferenceConfigStorage.enableGithubSearch
        enableCodeSearch.isSelected = quickReferenceConfigStorage.enableCodeSearch
    }

    fun checkNotModified(quickReferenceConfigStorage: QuickReferenceConfigStorage): Boolean {
        return (quickReferenceConfigStorage.enableQuickSearch == enableQuickSearch.isSelected
            && quickReferenceConfigStorage.enableGoogleSearch == enableGoogleSearch.isSelected
            && quickReferenceConfigStorage.enableBingSearch == enableBingSearch.isSelected
            && quickReferenceConfigStorage.enableStackOverflow == enableStackOverflow.isSelected
            && quickReferenceConfigStorage.enableGithubSearch == enableGithubSearch.isSelected
            && quickReferenceConfigStorage.enableCodeSearch == enableCodeSearch.isSelected)
    }

}
