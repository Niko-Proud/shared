package com.demoedu.campuscompass.data

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase

fun getCampusDatabaseBuilder(context: Context): RoomDatabase.Builder<CampusDatabase> {
    // The application context gives the whole app one shared database instance.
    val appContext = context.applicationContext
    // The named file preserves users, resources, and schedules between launches.
    val databaseFile = appContext.getDatabasePath("campus_compass.db")
    return Room.databaseBuilder<CampusDatabase>(
        context = appContext,
        name = databaseFile.absolutePath,
    )
}
