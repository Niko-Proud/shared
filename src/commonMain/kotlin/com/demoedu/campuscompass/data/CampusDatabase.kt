package com.demoedu.campuscompass.data

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabaseConstructor
import androidx.room3.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        CampusResourceEntity::class,
        CampusResourceAliasEntity::class,
        ResourceHoursEntity::class,
        ScheduleEntity::class,
        CampusMapEntity::class,
    ],
    // Version 9 adds a campus lookup index for fast campus-scoped searches.
    version = 9,
    exportSchema = true,
)
@ConstructedBy(CampusDatabaseConstructor::class)
abstract class CampusDatabase : RoomDatabase() {
    // Each DAO keeps one entity's reads and writes together.
    abstract fun userDao(): UserDao
    abstract fun campusResourceDao(): CampusResourceDao
    abstract fun campusResourceAliasDao(): CampusResourceAliasDao
    abstract fun resourceHoursDao(): ResourceHoursDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun campusMapDao(): CampusMapDao
}

@Suppress("KotlinNoActualForExpect")
expect object CampusDatabaseConstructor : RoomDatabaseConstructor<CampusDatabase>
