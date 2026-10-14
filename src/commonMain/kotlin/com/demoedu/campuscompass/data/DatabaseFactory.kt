package com.demoedu.campuscompass.data

import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

/**
 * Opens the shared campus database with durable storage and its schema history.
 * Both mobile platforms use the same data behavior.
 */
fun buildCampusDatabase(builder: RoomDatabase.Builder<CampusDatabase>): CampusDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .addMigrations(*CampusMigrations)
        .build()
