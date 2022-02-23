package com.quickref.plugin.action

import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.quickref.plugin.action.xml.ModifyColorsXmlDomParser
import java.io.File

class DarkValuesAction : BaseAction() {

    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val enabled = isAcceptableFile(file)
        e.presentation.isEnabled = enabled
        if (ActionPlaces.isPopupPlace(e.place)) {
            e.presentation.isVisible = enabled
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)

        if (file != null) {

            val dist = File(file.parent?.parent?.path, "values-night/colors.xml")
            println(dist.absolutePath)

            if (dist.exists()) {
                //TODO show a confirm dialog
            }

            val source = File(file.path)

            source.copyTo(dist, overwrite = true)

            ModifyColorsXmlDomParser.generatorXML(dist)
        }
    }

    fun isAcceptableFile(file: VirtualFile?): Boolean {
        return file != null && "xml".equals(file.extension, ignoreCase = true) && file.name == "colors.xml"
    }
}
