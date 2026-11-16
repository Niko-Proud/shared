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
    Migration(5, 6) { connection ->
        // Existing resources remain valid: descriptions are empty and the two references are null.
        // Keep this SQL in step with CampusResourceEntity and the exported schema in shared/schemas.
        // Room migration guide: https://developer.android.com/training/data-storage/room/migrating-db-versions
        connection.executeSQL(
            "ALTER TABLE CampusResources ADD COLUMN description TEXT NOT NULL DEFAULT ''",
        )
        connection.executeSQL(
            "ALTER TABLE CampusResources ADD COLUMN imageResource TEXT",
        )
        connection.executeSQL(
            "ALTER TABLE CampusResources ADD COLUMN officialUrl TEXT",
        )
    },
    Migration(6, 7) { connection ->
        // Maps are top-level navigation aids, so they do not share the resource foreign keys.
        // Keeping them independent lets one campus expose several map types without duplicating buildings.
        connection.executeSQL(
            """
            CREATE TABLE IF NOT EXISTS CampusMaps (
                mapId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                campusName TEXT NOT NULL,
                mapName TEXT NOT NULL,
                mapType TEXT NOT NULL,
                description TEXT NOT NULL,
                imageResource TEXT,
                officialUrl TEXT NOT NULL
            )
            """.trimIndent(),
        )
        connection.executeSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS index_CampusMaps_campusName_mapType ON CampusMaps(campusName, mapType)",
        )
    },
)
