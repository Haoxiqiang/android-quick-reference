package com.quickref.plugin.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.junit.Before
import org.junit.Test

class DBTest {

    private var database: QuickRefDB? = null

    @Before
    fun setup() {
        val classLoader = this.javaClass.classLoader
        val url = classLoader.getResource("db/QuickRefDB.db")
        val driver = JdbcSqliteDriver("jdbc:sqlite::resource:$url")
        database = QuickRefDB.invoke(driver)
        assert(database != null)
    }

    @Test
    fun testNativeFileMapping() {
        val nativeCount = database!!.nativeFileMappingQueries.selectAll().executeAsList().size
        println("nativeCount: $nativeCount")
        assert(nativeCount > 0)
    }

    @Test
    fun testJavaFileMapping() {
        val javaCount = database!!.javaFileMappingQueries.selectAll().executeAsList().size
        println("javaCount: $javaCount")
        assert(javaCount > 0)
    }
}
