package com.quickref.plugin.git

import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.util.function.Consumer

private const val aospDir = "/Users/haoxiqiang/opensource/platform_frameworks_base"

val aospRepository: Repository
    get() = getProjectGit()

private fun getProjectGit(): Repository {
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

private class StreamGobbler(inputStream: InputStream, consumer: Consumer<String>) : Runnable {
    private val inputStream: InputStream
    private val consumer: Consumer<String>

    init {
        this.inputStream = inputStream
        this.consumer = consumer
    }

    override fun run() {
        BufferedReader(InputStreamReader(inputStream))
            .lines()
            .forEach(consumer)
    }
}
