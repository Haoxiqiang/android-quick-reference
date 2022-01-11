package com.quickref.plugin.git

import com.quickref.plugin.version.AndroidVersion
import com.quickref.plugin.version.Source
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.json.JSONArray
import org.junit.Test
import java.io.File


class RepoUpdate {


    @Test
    fun copyAllBuildFiles() {
        val git = Git.wrap(aospRepository)

        val tagList = git.tagList().call()
        val target = "src/test/resources/cpp_files/"

        AndroidVersion
            .getVersionSource(Source.GithubAOSP)
            .versionPairs()
            .entries
            .forEach { entry ->
                val targetBranch = entry.value
                // checkout branch
                val tagRef = tagList.first { ref -> ref.name == "${Constants.R_TAGS}$targetBranch" }
                println("current ${aospRepository.branch} except:$targetBranch")
                try {
                    if (aospRepository.branch != tagRef.name) {
                        git.checkout()
                            .setName(tagRef.name)
                            .setForced(true)
                            .setForceRefUpdate(true)
                            .call()
                    }
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                    return@forEach
                }

                val repoDir = aospRepository.workTree

                val array = JSONArray()
                repoDir.walk()
                    .maxDepth(10)
                    .filter { file -> excludeDirs(file) }
                    .filter { file -> file.name.endsWith(".cpp") }
                    .forEach { file ->
                        val rp = file.parent.replace(repoDir.absolutePath, "")
                        array.put("$rp/${file.name}")
                    }

                val dest = File(target, "$targetBranch.json")
                dest.writeText(array.toString())
            }
    }

    private fun excludeDirs(file: File): Boolean {
        val path = file.absolutePath
        return !(path.contains("tool") || path.contains("cmds") || path.contains("tests"))
    }
}
