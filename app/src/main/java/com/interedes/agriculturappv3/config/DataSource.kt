package com.interedes.agriculturappv3.config

import com.raizlabs.android.dbflow.annotation.Database


@Database(name = DataSource.NAME, version = DataSource.VERSION, generatedClassSeparator = "_")
object DataSource {
    const val NAME: String = "db_agriculturapp"
    const val VERSION: Int = 1
}

/*
@Database(name = DataSource.NAME, version = DataSource.VERSION)
object DataSource {
    const val NAME = "db_agriculturapp"
    const val VERSION = 1
}
*/