package com.quickref.plugin.git

import com.quickref.plugin.extension.endsWithCLang
import com.quickref.plugin.version.AndroidVersion
import com.quickref.plugin.version.Source
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.junit.Test
import java.io.File


class RepoTools {

    @Test
    fun copyAllNativeFiles() {
        Git.wrap(aospRepository).use { git ->
            val tagList = git.tagList().call()
            AndroidVersion
                .getVersionSource(Source.GithubAOSP)
                .versionPairs()
                .entries
                .forEach { entry ->
                    val targetBranch = entry.value
                    val target = "src/test/resources/cpp_files/$targetBranch"
                    val tagRef = tagList.first { ref -> ref.name == "${Constants.R_TAGS}$targetBranch" }
                    println("current ${aospRepository.branch} except:$targetBranch")
                    try {
                        // checkout tag ref
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

                    repoDir.walk()
                        .maxDepth(depth = 10)
                        .filter { file -> excludeDirs(file) }
                        .filter { file -> file.name.endsWithCLang() }
                        .forEach { file ->
                            val rp = file.parent.replace(repoDir.absolutePath, "")
                            val tf = File("$target$rp/${file.name}")
                            file.copyTo(tf)
                        }
                }

        }
    }

    private fun excludeDirs(file: File): Boolean {
        val path = file.absolutePath
        return !(path.contains("tool") || path.contains("cmds") || path.contains("tests"))
    }
}