package com.demoedu.campuscompass.data

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabaseConstructor
import androidx.room3.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        CampusResourceEntity::class,
        ResourceHoursEntity::class,
        ScheduleEntity::class,
    ],
    // Version 5 includes the database rule that each username must be unique.
    version = 5,
    exportSchema = true,
)
@ConstructedBy(CampusDatabaseConstructor::class)
abstract class CampusDatabase : RoomDatabase() {
    // Provides the app's access point for users, resources, hours, and schedules.
    abstract fun campusDao(): CampusDao
}

@Suppress("KotlinNoActualForExpect")
expect object CampusDatabaseConstructor : RoomDatabaseConstructor<CampusDatabase>
