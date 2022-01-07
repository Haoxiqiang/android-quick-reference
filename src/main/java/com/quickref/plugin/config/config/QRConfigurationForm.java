package com.quickref.plugin.config.config;

import com.intellij.ui.IdeBorderFactory;
import com.quickref.plugin.config.QuickReferenceConfigStorage;
import org.jetbrains.annotations.NotNull;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.Objects;

public class QRConfigurationForm {

    private JPanel myMainPanel;

    private JCheckBox enableQuickSearch;
    private JCheckBox enableGoogleSearch;
    private JCheckBox enableBingSearch;
    private JCheckBox enableStackOverflow;
    private JCheckBox enableGithubSearch;
    private JCheckBox enableCodeSearch;

    private final QuickReferenceConfigStorage quickReferenceConfigStorage;

    public QRConfigurationForm() {
        this.quickReferenceConfigStorage = QuickReferenceConfigStorage.Companion.instance();
    }

    @NotNull
    public JComponent createPanel() {

        myMainPanel.setBorder(IdeBorderFactory.createTitledBorder("Detekt Settings"));

        enableQuickSearch.addChangeListener(changeEvent -> {
            boolean enabled = enableQuickSearch.isSelected();
            enableGoogleSearch.setEnabled(enabled);
            enableBingSearch.setEnabled(enabled);
            enableStackOverflow.setEnabled(enabled);
            enableGithubSearch.setEnabled(enabled);
            enableCodeSearch.setEnabled(enabled);
        });

        //FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(
        //    true,
        //    false,
        //    false,
        //    false,
        //    false,
        //    false);


        return myMainPanel;
    }

    public void apply() {
        quickReferenceConfigStorage.setEnableQuickSearch(enableQuickSearch.isSelected());
        quickReferenceConfigStorage.setEnableGoogleSearch(enableGoogleSearch.isSelected());
        quickReferenceConfigStorage.setEnableBingSearch(enableBingSearch.isSelected());
        quickReferenceConfigStorage.setEnableStackOverflow(enableStackOverflow.isSelected());
        quickReferenceConfigStorage.setEnableGithubSearch(enableGithubSearch.isSelected());
        quickReferenceConfigStorage.setEnableCodeSearch(enableCodeSearch.isSelected());
    }

    public void reset() {
        enableQuickSearch.setSelected(quickReferenceConfigStorage.getEnableQuickSearch());
        enableGoogleSearch.setSelected(quickReferenceConfigStorage.getEnableGoogleSearch());
        enableBingSearch.setSelected(quickReferenceConfigStorage.getEnableBingSearch());
        enableStackOverflow.setSelected(quickReferenceConfigStorage.getEnableStackOverflow());
        enableGithubSearch.setSelected(quickReferenceConfigStorage.getEnableGithubSearch());
        enableCodeSearch.setSelected(quickReferenceConfigStorage.getEnableCodeSearch());
    }

    public boolean isNotModified() {
        return Objects.equals(quickReferenceConfigStorage.getEnableQuickSearch(), enableQuickSearch.isSelected())
            && Objects.equals(quickReferenceConfigStorage.getEnableGoogleSearch(), enableGoogleSearch.isSelected())
            && Objects.equals(quickReferenceConfigStorage.getEnableBingSearch(), enableBingSearch.isSelected())
            && Objects.equals(quickReferenceConfigStorage.getEnableStackOverflow(), enableStackOverflow.isSelected())
            && Objects.equals(quickReferenceConfigStorage.getEnableGithubSearch(), enableGithubSearch.isSelected())
            && Objects.equals(quickReferenceConfigStorage.getEnableCodeSearch(), enableCodeSearch.isSelected())
            ;
    }
}
