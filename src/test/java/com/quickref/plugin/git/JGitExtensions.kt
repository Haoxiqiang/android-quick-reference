package com.quickref.plugin.git

import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File

enum class RepoType {
    AOSP_BASE,
    EXTERNAL_SKIA,
}

val aospBaseRepository: Repository
    get() = getProjectGit(RepoType.AOSP_BASE)

val skiaRepository: Repository
    get() = getProjectGit(RepoType.EXTERNAL_SKIA)

private fun getProjectGit(repoType: RepoType): Repository {
    val repoDir: String = getAOSPRootPath(repoType)
    val gitRepoDir = File(repoDir, ".git")
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

fun getAOSPRootPath(repoType: RepoType): String {
    return when (repoType) {
        RepoType.AOSP_BASE -> System.getenv("AOSP_PATH")
        RepoType.EXTERNAL_SKIA -> System.getenv("EXTERNAL_SKIA")
    }
}
