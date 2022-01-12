package com.quickref.plugin

import com.quickref.plugin.db.QuickRefDB
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.sqlite.JDBC
import java.io.File
import java.net.URL

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
        val classLoader = App.javaClass.classLoader
        val resource: URL = classLoader.getResource("db/QuickRefDB.db")!!
        val jdbcURL = "jdbc:sqlite::resource:${resource.toURI()}"
        // try call org.sqlite.JDBC.<cinit>
        JDBC.isValidURL(jdbcURL)
        val driver = JdbcSqliteDriver(jdbcURL)
        val database = QuickRefDB.invoke(driver)
        database
    }
}
