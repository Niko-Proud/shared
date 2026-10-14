package com.demoedu.campuscompass.data

import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

fun buildCampusDatabase(builder: RoomDatabase.Builder<CampusDatabase>): CampusDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .build()
