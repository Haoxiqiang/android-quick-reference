package com.quickref.plugin.config

import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.TitledSeparator
import com.intellij.util.ui.FormBuilder
import javax.swing.JCheckBox
import javax.swing.JPanel

internal object QRVersionsMenu {

    var enableAndroid35: JCheckBox = JCheckBox("android-15.0.0")
    var enableAndroid34: JCheckBox = JCheckBox("android-14.0.0")
    var enableAndroid33: JCheckBox = JCheckBox("android-13.0.0")
    var enableAndroid32: JCheckBox = JCheckBox("android-12.1.0-32")
    var enableAndroid31: JCheckBox = JCheckBox("android-12.0.0-31")
    var enableAndroid30: JCheckBox = JCheckBox("android-11.0.0-30")
    var enableAndroid29: JCheckBox = JCheckBox("android-10.0.0-29")
    var enableAndroid28: JCheckBox = JCheckBox("android-9.0.0-28")
    var enableAndroid27: JCheckBox = JCheckBox("android-8.1.0-27")
    var enableAndroid26: JCheckBox = JCheckBox("android-8.0.0-26")
    var enableAndroid25: JCheckBox = JCheckBox("android-7.1.2-25")
    var enableAndroid24: JCheckBox = JCheckBox("android-7.0.0-24")
    var enableAndroid23: JCheckBox = JCheckBox("android-6.0.1-23")
    var enableAndroid22: JCheckBox = JCheckBox("android-5.1.1-22")
    var enableAndroid21: JCheckBox = JCheckBox("android-5.0.2-21")
    var enableAndroid20: JCheckBox = JCheckBox("android-4.4w-20")
    var enableAndroid19: JCheckBox = JCheckBox("android-4.4.4-19")
    var enableAndroid18: JCheckBox = JCheckBox("android-4.3.1-18")
    var enableAndroid17: JCheckBox = JCheckBox("android-4.2.2-17")
    var enableAndroid16: JCheckBox = JCheckBox("android-4.1.2-16")
    var enableAndroid15: JCheckBox = JCheckBox("android-4.0.4-15")
    var enableAndroid14: JCheckBox = JCheckBox("android-4.0.2-14")
    var enableAndroid13: JCheckBox = JCheckBox("android-3.2.4-13")
    var enableAndroid10: JCheckBox = JCheckBox("android-2.3.7-10")
    var enableAndroid08: JCheckBox = JCheckBox("android-2.2.3-8")
    var enableAndroid07: JCheckBox = JCheckBox("android-2.1-7")
    var enableAndroid06: JCheckBox = JCheckBox("android-2.0.1-6")
    var enableAndroid05: JCheckBox = JCheckBox("android-2.0-5")
    var enableAndroid04: JCheckBox = JCheckBox("android-1.6-4")

    var container: JPanel = FormBuilder.createFormBuilder()
        .addComponent(TitledSeparator("Android Versions"))
        .setFormLeftIndent(IdeBorderFactory.TITLED_BORDER_INDENT)
        .addComponent(enableAndroid35)
        .addComponent(enableAndroid34)
        .addComponent(enableAndroid33)
        .addComponent(enableAndroid32)
        .addComponent(enableAndroid31)
        .addComponent(enableAndroid30)
        .addComponent(enableAndroid29)
        .addComponent(enableAndroid28)
        .addComponent(enableAndroid27)
        .addComponent(enableAndroid26)
        .addComponent(enableAndroid25)
        .addComponent(enableAndroid24)
        .addComponent(enableAndroid23)
        .addComponent(enableAndroid22)
        .addComponent(enableAndroid21)
        .addComponent(enableAndroid20)
        .addComponent(enableAndroid19)
        .addComponent(enableAndroid18)
        .addComponent(enableAndroid17)
        .addComponent(enableAndroid16)
        .addComponent(enableAndroid15)
        .addComponent(enableAndroid14)
        .addComponent(enableAndroid13)
        .addComponent(enableAndroid10)
        .addComponent(enableAndroid08)
        .addComponent(enableAndroid07)
        .addComponent(enableAndroid06)
        .addComponent(enableAndroid05)
        .addComponent(enableAndroid04)
        .setFormLeftIndent(IdeBorderFactory.TITLED_BORDER_LEFT_INSET)
        .panel

    fun applyState(quickReferenceConfigStorage: QuickReferenceConfigStorage) {
        quickReferenceConfigStorage.enableAndroid35 = enableAndroid35.isSelected
        quickReferenceConfigStorage.enableAndroid34 = enableAndroid34.isSelected
        quickReferenceConfigStorage.enableAndroid33 = enableAndroid33.isSelected
        quickReferenceConfigStorage.enableAndroid32 = enableAndroid32.isSelected
        quickReferenceConfigStorage.enableAndroid31 = enableAndroid31.isSelected
        quickReferenceConfigStorage.enableAndroid30 = enableAndroid30.isSelected
        quickReferenceConfigStorage.enableAndroid29 = enableAndroid29.isSelected
        quickReferenceConfigStorage.enableAndroid28 = enableAndroid28.isSelected
        quickReferenceConfigStorage.enableAndroid27 = enableAndroid27.isSelected
        quickReferenceConfigStorage.enableAndroid26 = enableAndroid26.isSelected
        quickReferenceConfigStorage.enableAndroid25 = enableAndroid25.isSelected
        quickReferenceConfigStorage.enableAndroid24 = enableAndroid24.isSelected
        quickReferenceConfigStorage.enableAndroid23 = enableAndroid23.isSelected
        quickReferenceConfigStorage.enableAndroid22 = enableAndroid22.isSelected
        quickReferenceConfigStorage.enableAndroid21 = enableAndroid21.isSelected
        quickReferenceConfigStorage.enableAndroid20 = enableAndroid20.isSelected
        quickReferenceConfigStorage.enableAndroid19 = enableAndroid19.isSelected
        quickReferenceConfigStorage.enableAndroid18 = enableAndroid18.isSelected
        quickReferenceConfigStorage.enableAndroid17 = enableAndroid17.isSelected
        quickReferenceConfigStorage.enableAndroid16 = enableAndroid16.isSelected
        quickReferenceConfigStorage.enableAndroid15 = enableAndroid15.isSelected
        quickReferenceConfigStorage.enableAndroid14 = enableAndroid14.isSelected
        quickReferenceConfigStorage.enableAndroid13 = enableAndroid13.isSelected
        quickReferenceConfigStorage.enableAndroid10 = enableAndroid10.isSelected
        quickReferenceConfigStorage.enableAndroid08 = enableAndroid08.isSelected
        quickReferenceConfigStorage.enableAndroid07 = enableAndroid07.isSelected
        quickReferenceConfigStorage.enableAndroid06 = enableAndroid06.isSelected
        quickReferenceConfigStorage.enableAndroid05 = enableAndroid05.isSelected
    }

    fun loadState(quickReferenceConfigStorage: QuickReferenceConfigStorage) {
        enableAndroid35.isSelected = quickReferenceConfigStorage.enableAndroid35
        enableAndroid34.isSelected = quickReferenceConfigStorage.enableAndroid34
        enableAndroid33.isSelected = quickReferenceConfigStorage.enableAndroid33
        enableAndroid32.isSelected = quickReferenceConfigStorage.enableAndroid32
        enableAndroid31.isSelected = quickReferenceConfigStorage.enableAndroid31
        enableAndroid30.isSelected = quickReferenceConfigStorage.enableAndroid30
        enableAndroid29.isSelected = quickReferenceConfigStorage.enableAndroid29
        enableAndroid28.isSelected = quickReferenceConfigStorage.enableAndroid28
        enableAndroid27.isSelected = quickReferenceConfigStorage.enableAndroid27
        enableAndroid26.isSelected = quickReferenceConfigStorage.enableAndroid26
        enableAndroid25.isSelected = quickReferenceConfigStorage.enableAndroid25
        enableAndroid24.isSelected = quickReferenceConfigStorage.enableAndroid24
        enableAndroid23.isSelected = quickReferenceConfigStorage.enableAndroid23
        enableAndroid22.isSelected = quickReferenceConfigStorage.enableAndroid22
        enableAndroid21.isSelected = quickReferenceConfigStorage.enableAndroid21
        enableAndroid20.isSelected = quickReferenceConfigStorage.enableAndroid20
        enableAndroid19.isSelected = quickReferenceConfigStorage.enableAndroid19
        enableAndroid18.isSelected = quickReferenceConfigStorage.enableAndroid18
        enableAndroid17.isSelected = quickReferenceConfigStorage.enableAndroid17
        enableAndroid16.isSelected = quickReferenceConfigStorage.enableAndroid16
        enableAndroid15.isSelected = quickReferenceConfigStorage.enableAndroid15
        enableAndroid14.isSelected = quickReferenceConfigStorage.enableAndroid14
        enableAndroid13.isSelected = quickReferenceConfigStorage.enableAndroid13
        enableAndroid10.isSelected = quickReferenceConfigStorage.enableAndroid10
        enableAndroid08.isSelected = quickReferenceConfigStorage.enableAndroid08
        enableAndroid07.isSelected = quickReferenceConfigStorage.enableAndroid07
        enableAndroid06.isSelected = quickReferenceConfigStorage.enableAndroid06
        enableAndroid05.isSelected = quickReferenceConfigStorage.enableAndroid05
        enableAndroid04.isSelected = quickReferenceConfigStorage.enableAndroid04
    }

    fun checkNotModified(quickReferenceConfigStorage: QuickReferenceConfigStorage): Boolean {
        return (
            quickReferenceConfigStorage.enableAndroid35 == enableAndroid35.isSelected &&
                quickReferenceConfigStorage.enableAndroid34 == enableAndroid34.isSelected &&
                quickReferenceConfigStorage.enableAndroid33 == enableAndroid33.isSelected &&
                quickReferenceConfigStorage.enableAndroid32 == enableAndroid32.isSelected &&
                quickReferenceConfigStorage.enableAndroid31 == enableAndroid31.isSelected &&
                quickReferenceConfigStorage.enableAndroid30 == enableAndroid30.isSelected &&
                quickReferenceConfigStorage.enableAndroid29 == enableAndroid29.isSelected &&
                quickReferenceConfigStorage.enableAndroid28 == enableAndroid28.isSelected &&
                quickReferenceConfigStorage.enableAndroid27 == enableAndroid27.isSelected &&
                quickReferenceConfigStorage.enableAndroid26 == enableAndroid26.isSelected &&
                quickReferenceConfigStorage.enableAndroid25 == enableAndroid25.isSelected &&
                quickReferenceConfigStorage.enableAndroid24 == enableAndroid24.isSelected &&
                quickReferenceConfigStorage.enableAndroid23 == enableAndroid23.isSelected &&
                quickReferenceConfigStorage.enableAndroid22 == enableAndroid22.isSelected &&
                quickReferenceConfigStorage.enableAndroid21 == enableAndroid21.isSelected &&
                quickReferenceConfigStorage.enableAndroid20 == enableAndroid20.isSelected &&
                quickReferenceConfigStorage.enableAndroid19 == enableAndroid19.isSelected &&
                quickReferenceConfigStorage.enableAndroid18 == enableAndroid18.isSelected &&
                quickReferenceConfigStorage.enableAndroid17 == enableAndroid17.isSelected &&
                quickReferenceConfigStorage.enableAndroid16 == enableAndroid16.isSelected &&
                quickReferenceConfigStorage.enableAndroid15 == enableAndroid15.isSelected &&
                quickReferenceConfigStorage.enableAndroid14 == enableAndroid14.isSelected &&
                quickReferenceConfigStorage.enableAndroid13 == enableAndroid13.isSelected &&
                quickReferenceConfigStorage.enableAndroid10 == enableAndroid10.isSelected &&
                quickReferenceConfigStorage.enableAndroid08 == enableAndroid08.isSelected &&
                quickReferenceConfigStorage.enableAndroid07 == enableAndroid07.isSelected &&
                quickReferenceConfigStorage.enableAndroid06 == enableAndroid06.isSelected &&
                quickReferenceConfigStorage.enableAndroid05 == enableAndroid05.isSelected &&
                quickReferenceConfigStorage.enableAndroid04 == enableAndroid04.isSelected
            )
    }

}
