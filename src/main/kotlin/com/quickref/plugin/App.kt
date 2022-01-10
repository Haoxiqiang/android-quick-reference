package com.quickref.plugin

import com.quickref.plugin.db.QuickRefDB
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.File
import java.io.FileInputStream
import java.util.Properties

object App {
    val charSet = Charsets.UTF_8
    const val AppTitle = "Quick Reference"
    const val notificationGroup = "com.quickref.plugin.notification"

    val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    val mainScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

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

    // 全局配置文件
    private val GLOBAL_PROPS: File = File(PROJECT_DIR, "global.properties")

    val appProps = Properties().also {
        if (GLOBAL_PROPS.isDirectory) {
            GLOBAL_PROPS.delete()
        }
        if (GLOBAL_PROPS.exists().not()) {
            GLOBAL_PROPS.createNewFile()
        }
        it.load(FileInputStream(GLOBAL_PROPS))
    }

    val db by lazy {
        val driver = JdbcSqliteDriver("jdbc:sqlite::resource:${javaClass.classLoader.getResource("db/QuickRefDB.db")}")
        //QuickRefDB.Schema.create(driver)
        val database = QuickRefDB.invoke(driver)
        database
    }
}
