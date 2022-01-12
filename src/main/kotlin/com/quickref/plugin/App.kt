package com.quickref.plugin

import com.quickref.plugin.db.QuickRefDB
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import java.io.File
import java.net.URL
import java.net.URLDecoder

object App {
    val charSet = Charsets.UTF_8
    const val AppTitle = "Quick Reference"
    const val notificationGroup = "com.quickref.plugin.notification"

    // 用户目录
    private val USER_HOME: String = System.getProperty("user.home")

    // 基础目录
    private val PROJECT_DIR: File = File(USER_HOME, ".quick-reference").also {
        it.mkdirs()
    }

    // 缓存目录
    val CACHE_DIR: File = File(PROJECT_DIR, "cache").also {
        it.mkdirs()
    }

    val db by lazy {
        val classLoader = ImageAssets.javaClass.classLoader
        val path: URL = classLoader.getResource("db/QuickRefDB.db")!!
        val file = URLDecoder.decode(path.toString(), charSet)
        val driver = JdbcSqliteDriver("jdbc:sqlite::resource:$file")
        val database = QuickRefDB.invoke(driver)
        database
    }
}
