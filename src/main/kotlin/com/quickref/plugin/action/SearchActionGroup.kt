package com.quickref.plugin.action

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.quickref.plugin.ImageAssets
import com.quickref.plugin.viewer.CodeSearchViewer

class SearchActionGroup : ActionGroup() {

    override fun update(event: AnActionEvent) {
        //val editor = event.getData(CommonDataKeys.EDITOR)
        //event.presentation.isEnabled = editor != null
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return arrayOf(
            KeywordSearchAction(
                "Google".padEnd(15),
                "Use the Google's search.", ImageAssets.GOOGLE
            ) { keyword ->
                SearchOnline.googleSearch(keyword)
            },
            KeywordSearchAction(
                "Bing".padEnd(15),
                "Use the Bing's search.", ImageAssets.BING
            ) { keyword ->
                SearchOnline.bingSearch(keyword)
            },
            KeywordSearchAction(
                "GitHub".padEnd(15),
                "Use the GitHub's search.", ImageAssets.GITHUB
            ) { keyword ->
                SearchOnline.githubSearch(keyword)
            },
            KeywordSearchAction(
                "StackOverflow".padEnd(15),
                "Use the StackOverflow's search.", ImageAssets.STACKOVERFLOW
            ) { keyword ->
                SearchOnline.stackoverflowSearch(keyword)
            },
            KeywordSearchAction(
                "CodeSearch".padEnd(15),
                "Use the AndroidCS's search.", ImageAssets.CODESEARCH
            ) { keyword ->
                CodeSearchViewer.search(keyword)
            },
        )
    }
}
