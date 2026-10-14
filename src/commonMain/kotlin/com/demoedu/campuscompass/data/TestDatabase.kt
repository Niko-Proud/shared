package com.demoedu.campuscompass.data

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

/** Creates an isolated database suitable for shared/common tests. */
fun createInMemoryCampusDatabase(): CampusDatabase =
    Room.inMemoryDatabaseBuilder<CampusDatabase>(
        factory = CampusDatabaseConstructor::initialize,
    )
        .setDriver(BundledSQLiteDriver())
        .build()
