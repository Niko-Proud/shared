package com.demoedu.campuscompass.data

import androidx.room3.migration.Migration
import androidx.sqlite.async.executeSQL

/**
 * Keeps existing user data available as the database gains new capabilities.
 * The same schema change is applied consistently on Android and iOS.
 */
val CampusMigrations: Array<Migration> = arrayOf(
    Migration(4, 5) { connection ->
        // This makes username uniqueness part of the stored data rules, not just the UI.
        connection.executeSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS index_Users_username ON Users(username)",
        )
    },
)
