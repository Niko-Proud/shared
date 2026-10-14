package com.demoedu.campuscompass.data

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabaseConstructor
import androidx.room3.RoomDatabase
import androidx.room3.TypeConverters

@Database(
    entities = [
        UserEntity::class,
        CampusResourceEntity::class,
        ScheduleEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
@TypeConverters(HoursOfOperationConverter::class)
@ConstructedBy(CampusDatabaseConstructor::class)
abstract class CampusDatabase : RoomDatabase() {
    abstract fun campusDao(): CampusDao
}

@Suppress("KotlinNoActualForExpect")
expect object CampusDatabaseConstructor : RoomDatabaseConstructor<CampusDatabase>
