package com.quickref.plugin.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.junit.Test

class DBTest {

    @Test
    fun testBitmapMapping() {

        val classLoader = this.javaClass.classLoader
        val url = classLoader.getResource("db/QuickRefDB.db")
        val driver = JdbcSqliteDriver("jdbc:sqlite::resource:$url")
        val database = QuickRefDB.invoke(driver)
        println(database.nativeFileMappingQueries.selectAll().executeAsList().size)
    }
}
