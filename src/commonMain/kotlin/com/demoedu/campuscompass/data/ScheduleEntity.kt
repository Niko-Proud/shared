package com.demoedu.campuscompass.data

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

/** Represents one user's planned visit to one resource on one day and time range. */
@Entity(
    tableName = "Schedules",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["usrId"],
            childColumns = ["usrId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CampusResourceEntity::class,
            parentColumns = ["resourceId"],
            childColumns = ["resourceId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("usrId"), Index("resourceId")],
)
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val scheduleId: Long = 0,
    val usrId: Long, // Owns the schedule entry and enforces user-specific results.
    val resourceId: Long, // Points to the building/resource selected by the user.
    val scheduleName: String, // User-facing name for the planned item.
    val dayOfWeek: Int, // Ordered weekday value used by schedule sorting.
    val startTime: String, // Display-ready start time.
    val endTime: String, // Display-ready end time.
    val notes: String? = null, // Optional context for the planned visit.
)

/** Combined schedule and resource data used when a saved schedule is displayed. */
data class ScheduleWithResource(
    val scheduleId: Long,
    val usrId: Long,
    val resourceId: Long,
    val scheduleName: String,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
    val notes: String?,
    val campusName: String,
    val buildingName: String,
    val buildingNumber: String,
    val resourceType: String,
    val resourceTag: String,
    val availability: Boolean,
    val monday: String?,
    val tuesday: String?,
    val wednesday: String?,
    val thursday: String?,
    val friday: String?,
    val saturday: String?,
    val sunday: String?,
)
