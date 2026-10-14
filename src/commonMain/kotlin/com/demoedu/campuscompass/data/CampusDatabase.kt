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
    // Each DAO keeps one entity's reads and writes together.
    abstract fun userDao(): UserDao
    abstract fun campusResourceDao(): CampusResourceDao
    abstract fun resourceHoursDao(): ResourceHoursDao
    abstract fun scheduleDao(): ScheduleDao
}

@Suppress("KotlinNoActualForExpect")
expect object CampusDatabaseConstructor : RoomDatabaseConstructor<CampusDatabase>
