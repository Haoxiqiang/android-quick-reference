package  com.quickref.plugin.config

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.SearchableConfigurable
import com.quickref.plugin.App
import javax.swing.JComponent

class QuickReferenceConfig : SearchableConfigurable, Configurable.VariableProjectAppLevel {

    private val qrConfigurationForm: QRConfigurationForm = QRConfigurationForm()

    override fun isModified(): Boolean = !qrConfigurationForm.isNotModified

    override fun getId(): String = "preferences.quick.reference.config"

    override fun getDisplayName(): String = App.AppTitle

    override fun apply() {
        qrConfigurationForm.apply()
    }

    override fun reset() = qrConfigurationForm.reset()

    override fun createComponent(): JComponent = qrConfigurationForm.createPanel()

    override fun isProjectLevel(): Boolean {
        return false
    }
}
