package com.demoedu.campuscompass.data

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabaseConstructor
import androidx.room3.RoomDatabase

@Database(
    entities = [UserEntity::class, CampusResourceEntity::class, ResourceHoursEntity::class],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(CampusDatabaseConstructor::class)
abstract class CampusDatabase : RoomDatabase() {
    abstract fun campusDao(): CampusDao
}

@Suppress("KotlinNoActualForExpect")
expect object CampusDatabaseConstructor : RoomDatabaseConstructor<CampusDatabase>
