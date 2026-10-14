package com.demoedu.campuscompass.data

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

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
    val monday: String,
    val tuesday: String,
    val wednesday: String,
    val thursday: String,
    val friday: String,
    val saturday: String,
    val sunday: String,
)
