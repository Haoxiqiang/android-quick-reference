package com.quickref.plugin

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.quickref.plugin.db.QuickRefDB
import java.io.File
import java.net.URL

object App {
    const val AppTitle = "Quick Reference"

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
        val classLoader = App.javaClass.classLoader
        val resource: URL = classLoader.getResource("db/QuickRefDB.db")!!
        val jdbcURL = "jdbc:sqlite::resource:${resource.toURI()}"
        // try call org.sqlite.JDBC.<cinit>
        Class.forName("org.sqlite.JDBC")
        val driver = JdbcSqliteDriver(jdbcURL)
        val database = QuickRefDB.invoke(driver)
        database
    }
}
