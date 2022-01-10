package com.quickref.plugin.db

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.junit.Test

class NativeFileMappingTest {

    @Test
    fun testBitmapMapping() {

        val driver = JdbcSqliteDriver("jdbc:sqlite::resource:${javaClass.classLoader.getResource("db/QuickRefDB.db")}")
        //QuickRefDB.Schema.create(driver)
        val database = QuickRefDB.invoke(driver)

        val nativeFileMappingQueries: NativeFileMappingQueries = database.nativeFileMappingQueries

        val bitmapFile29 = nativeFileMappingQueries.getNativeFile("android/graphics/Bitmap.cpp", 29).executeAsOne()
        val bitmapFile30 =
            nativeFileMappingQueries.getNativeFile("android/graphics/Bitmap.cpp", 30).executeAsOneOrNull()

        assert(bitmapFile29 == "core/jni/android/graphics/Bitmap.cpp")
        assert(bitmapFile30 == "libs/hwui/jni/Bitmap.cpp")
    }
}
