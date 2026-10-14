package com.demoedu.campuscompass.data

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "CampusResources",
    indices = [Index(value = ["resourceTag"], unique = true)],
)
data class CampusResourceEntity(
    @PrimaryKey(autoGenerate = true)
    val resourceId: Long = 0,
    val campusName: String,
    val buildingName: String,
    val buildingNumber: String,
    val resourceType: String,
    val resourceTag: String,
    val availability: Boolean = true,
)
