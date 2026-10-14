package com.demoedu.campuscompass.data

import kotlinx.cinterop.ExperimentalForeignApi

import androidx.room3.Room
import androidx.room3.RoomDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
fun getCampusDatabaseBuilder(): RoomDatabase.Builder<CampusDatabase> {
    // The app's document storage keeps the shared database available between launches.
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )?.path ?: error("Unable to locate the iOS documents directory")

    // A stable filename ensures the app continues using the same campus data.
    return Room.databaseBuilder<CampusDatabase>(
        name = "$documentDirectory/campus_compass.db",
    )
}
