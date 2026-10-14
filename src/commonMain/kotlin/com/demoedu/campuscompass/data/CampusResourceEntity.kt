package com.demoedu.campuscompass.data

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

/** Stores the building and resource details shown by the campus search screen. */
@Entity(
    tableName = "CampusResources",
    indices = [Index(value = ["resourceTag"], unique = true)],
)
data class CampusResourceEntity(
    @PrimaryKey(autoGenerate = true)
    val resourceId: Long = 0,
    val campusName: String, // Identifies the campus used by the first filter.
    val buildingName: String, // Human-readable structure name used by building search.
    val buildingNumber: String, // Official number or identifier used by building search.
    val resourceType: String, // Groups resources such as labs, study spaces, or services.
    val resourceTag: String, // Stable resource label used for unique resource lookup.
    val availability: Boolean = true, // Current availability shown on the resource card.
)
