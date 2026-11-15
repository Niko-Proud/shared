package com.demoedu.campuscompass.data

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

/** Stores a campus-wide map separately from individual buildings and services. */
@Entity(
    tableName = "CampusMaps",
    indices = [Index(value = ["campusName", "mapType"], unique = true)],
)
data class CampusMapEntity(
    @PrimaryKey(autoGenerate = true)
    val mapId: Long = 0,
    val campusName: String,
    val mapName: String,
    val mapType: String,
    val description: String,
    val imageResource: String? = null,
    val officialUrl: String,
)
