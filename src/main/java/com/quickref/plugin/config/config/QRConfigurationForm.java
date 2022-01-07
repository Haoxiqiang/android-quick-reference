package com.quickref.plugin.config.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.IdeBorderFactory;
import com.quickref.plugin.App;
import com.quickref.plugin.config.QuickReferenceConfigStorage;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Objects;

public class QRConfigurationForm {

    private JPanel myMainPanel;

    private JCheckBox enableQuickSearch;
    private JCheckBox enableGoogleSearch;
    private JCheckBox enableBingSearch;
    private JCheckBox enableStackOverflow;
    private JCheckBox enableGithubSearch;
    private JCheckBox enableCodeSearch;

    private JButton cleanCache;
    private JLabel cacheSize;

    private final QuickReferenceConfigStorage quickReferenceConfigStorage;

    public QRConfigurationForm() {
        this.quickReferenceConfigStorage = QuickReferenceConfigStorage.Companion.instance();
    }

    @NotNull
    public JComponent createPanel() {

        myMainPanel.setBorder(IdeBorderFactory.createTitledBorder("Quick Reference Settings"));

        enableQuickSearch.addChangeListener(changeEvent -> {
            boolean enabled = enableQuickSearch.isSelected();
            enableGoogleSearch.setEnabled(enabled);
            enableBingSearch.setEnabled(enabled);
            enableStackOverflow.setEnabled(enabled);
            enableGithubSearch.setEnabled(enabled);
            enableCodeSearch.setEnabled(enabled);
        });

        cacheSize.setText("...");

        calCacheSize();

        cleanCache.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    FileUtils.cleanDirectory(App.INSTANCE.getCACHE_DIR());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                calCacheSize();
            }
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

    private void calCacheSize() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                final long size = FileUtils.sizeOfDirectory(App.INSTANCE.getCACHE_DIR());
                cacheSize.setText(String.format("Currently Storage : %.2fM", size / 1024.0 / 1024.0));
            }
        });
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
