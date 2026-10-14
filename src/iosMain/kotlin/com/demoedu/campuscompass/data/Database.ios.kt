package com.demoedu.campuscompass.data

import androidx.room3.Room
import androidx.room3.RoomDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getCampusDatabaseBuilder(): RoomDatabase.Builder<CampusDatabase> {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )?.path ?: error("Unable to locate the iOS documents directory")

    return Room.databaseBuilder<CampusDatabase>(
        name = "$documentDirectory/campus_compass.db",
    )
}
