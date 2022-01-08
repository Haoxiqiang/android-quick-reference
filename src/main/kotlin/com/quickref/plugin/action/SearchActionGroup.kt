package com.quickref.plugin.action

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.quickref.plugin.ImageAssets
import com.quickref.plugin.config.QuickReferenceConfigStorage
import com.quickref.plugin.viewer.CodeSearchViewer

class SearchActionGroup : ActionGroup() {
    companion object {
        const val MENU_TEXT_LENGTH = 15
    }

    private val quickReferenceConfigStorage by lazy {
        QuickReferenceConfigStorage.instance()
    }

    override fun update(event: AnActionEvent) {
        val project = event.project
        event.presentation.isEnabled =
            project != null && quickReferenceConfigStorage.enableQuickSearch
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val searchActions = mutableListOf<KeywordSearchAction>()

        if (quickReferenceConfigStorage.enableGoogleSearch) {
            searchActions.add(KeywordSearchAction(
                "Google".padEnd(MENU_TEXT_LENGTH),
                "Use the Google's search.", ImageAssets.GOOGLE
            ) { keyword ->
                SearchOnline.googleSearch(keyword)
            })
        }
        if (quickReferenceConfigStorage.enableBingSearch) {
            searchActions.add(KeywordSearchAction(
                "Bing".padEnd(MENU_TEXT_LENGTH),
                "Use the Bing's search.", ImageAssets.BING
            ) { keyword ->
                SearchOnline.bingSearch(keyword)
            })
        }
        if (quickReferenceConfigStorage.enableStackOverflow) {
            searchActions.add(KeywordSearchAction(
                "StackOverflow".padEnd(MENU_TEXT_LENGTH),
                "Use the StackOverflow's search.", ImageAssets.STACKOVERFLOW
            ) { keyword ->
                SearchOnline.stackoverflowSearch(keyword)
            })
        }
        if (quickReferenceConfigStorage.enableGithubSearch) {
            searchActions.add(KeywordSearchAction(
                "GitHub".padEnd(MENU_TEXT_LENGTH),
                "Use the GitHub's search.", ImageAssets.GITHUB
            ) { keyword ->
                SearchOnline.githubSearch(keyword)
            })
        }
        if (quickReferenceConfigStorage.enableCodeSearch) {
            searchActions.add(KeywordSearchAction(
                "CodeSearch".padEnd(MENU_TEXT_LENGTH),
                "Use the AndroidCS's search.", ImageAssets.CODESEARCH
            ) { keyword ->
                CodeSearchViewer.search(keyword)
            })
        }
        return searchActions.toTypedArray()
    }
}
