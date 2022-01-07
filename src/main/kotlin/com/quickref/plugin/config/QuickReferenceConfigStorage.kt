package com.quickref.plugin.config

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Tag

@State(
    name = "QuickReferenceProjectConfiguration",
    storages = [Storage("quick-reference.xml")]
)
class QuickReferenceConfigStorage : PersistentStateComponent<QuickReferenceConfigStorage> {

    companion object {
        fun instance(): QuickReferenceConfigStorage {
            return ApplicationManager.getApplication().getService(QuickReferenceConfigStorage::class.java)
        }
    }

    // quick search
    @Tag
    var enableQuickSearch: Boolean = true

    @Tag
    var enableGoogleSearch: Boolean = true

    @Tag
    var enableBingSearch: Boolean = true

    @Tag
    var enableStackOverflow: Boolean = true

    @Tag
    var enableGithubSearch: Boolean = true

    @Tag
    var enableCodeSearch: Boolean = true

    // more config

    override fun getState(): QuickReferenceConfigStorage = this

    override fun loadState(state: QuickReferenceConfigStorage) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
