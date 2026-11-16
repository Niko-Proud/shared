package com.demoedu.campuscompass.data

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

/** Keeps operating hours separate so every resource can have a value for each weekday. */
@Entity(
    tableName = "ResourceHours",
    foreignKeys = [
        ForeignKey(
            entity = CampusResourceEntity::class,
            parentColumns = ["resourceId"],
            childColumns = ["resourceId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class ResourceHoursEntity(
    @PrimaryKey
    val resourceId: Long,
    val monday: String, // Monday hours or a value such as "Closed".
    val tuesday: String, // Tuesday hours.
    val wednesday: String, // Wednesday hours.
    val thursday: String, // Thursday hours.
    val friday: String, // Friday hours.
    val saturday: String, // Saturday hours.
    val sunday: String, // Sunday hours.
)
