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

    // quick search.
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

    // android versions.
    @Tag
    var enableAndroid31: Boolean = true

    @Tag
    var enableAndroid30: Boolean = true

    @Tag
    var enableAndroid29: Boolean = true

    @Tag
    var enableAndroid28: Boolean = true

    @Tag
    var enableAndroid27: Boolean = true

    @Tag
    var enableAndroid26: Boolean = true

    @Tag
    var enableAndroid25: Boolean = true

    @Tag
    var enableAndroid24: Boolean = true

    @Tag
    var enableAndroid23: Boolean = true

    @Tag
    var enableAndroid22: Boolean = true

    @Tag
    var enableAndroid21: Boolean = true

    @Tag
    var enableAndroid20: Boolean = false

    @Tag
    var enableAndroid19: Boolean = false

    @Tag
    var enableAndroid18: Boolean = false

    @Tag
    var enableAndroid17: Boolean = false

    @Tag
    var enableAndroid16: Boolean = false

    @Tag
    var enableAndroid15: Boolean = false

    @Tag
    var enableAndroid14: Boolean = false

    @Tag
    var enableAndroid13: Boolean = false

    @Tag
    var enableAndroid10: Boolean = false

    @Tag
    var enableAndroid08: Boolean = false

    @Tag
    var enableAndroid07: Boolean = false

    @Tag
    var enableAndroid06: Boolean = false

    @Tag
    var enableAndroid05: Boolean = false

    @Tag
    var enableAndroid04: Boolean = false

    override fun getState(): QuickReferenceConfigStorage = this

    override fun loadState(state: QuickReferenceConfigStorage) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
