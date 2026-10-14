package com.demoedu.campuscompass.data

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

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
    val usrId: Long,
    val resourceId: Long,
    val scheduleName: String,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
    val notes: String? = null,
)

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
