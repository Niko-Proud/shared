package com.demoedu.campuscompass.data

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase

fun getCampusDatabaseBuilder(context: Context): RoomDatabase.Builder<CampusDatabase> {
    val appContext = context.applicationContext
    val databaseFile = appContext.getDatabasePath("campus_compass.db")
    return Room.databaseBuilder<CampusDatabase>(
        context = appContext,
        name = databaseFile.absolutePath,
    )
}
