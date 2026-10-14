package com.demoedu.campuscompass.data

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
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
    indices = [Index("resourceId")],
)
data class ResourceHoursEntity(
    @PrimaryKey(autoGenerate = true)
    val hoursId: Long = 0,
    val resourceId: Long,
    val dayOfWeek: Int,
    val opensAt: String,
    val closesAt: String,
    val isClosed: Boolean = false,
)
