package com.quickref.plugin.git

import com.intellij.openapi.diagnostic.thisLogger
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File

val aospRepository: Repository
    get() = getProjectGit()

private fun getProjectGit(): Repository {
    val aospDir: String = System.getenv("AOSP_PATH")
    val gitRepoDir = File(aospDir, ".git")
    val git = if (gitRepoDir.isDirectory) {
        gitRepoDir
    } else {
        val text = gitRepoDir.readText()
        val pathLength = gitRepoDir.absolutePath.length
        val path = gitRepoDir.absolutePath.substring(
            0,
            pathLength - ".git".length
        ) + text.substring("gitdir: ".length)
        File(path.trim())
    }

    return FileRepositoryBuilder()
        .findGitDir(git)
        .build()
}
