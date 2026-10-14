package com.demoedu.campuscompass.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query

@Dao
interface ScheduleDao {
    @Insert
    suspend fun insertSchedule(schedule: ScheduleEntity): Long

    @Query("SELECT scheduleId FROM Schedules WHERE scheduleId = :scheduleId")
    suspend fun getScheduleId(scheduleId: Long): Long?

    @Query("SELECT usrId FROM Schedules WHERE scheduleId = :scheduleId")
    suspend fun getScheduleUserId(scheduleId: Long): Long?

    @Query("SELECT resourceId FROM Schedules WHERE scheduleId = :scheduleId")
    suspend fun getScheduleResourceId(scheduleId: Long): Long?

    @Query("SELECT scheduleName FROM Schedules WHERE scheduleId = :scheduleId")
    suspend fun getScheduleName(scheduleId: Long): String?

    @Query("SELECT dayOfWeek FROM Schedules WHERE scheduleId = :scheduleId")
    suspend fun getScheduleDayOfWeek(scheduleId: Long): Int?

    @Query("SELECT startTime FROM Schedules WHERE scheduleId = :scheduleId")
    suspend fun getScheduleStartTime(scheduleId: Long): String?

    @Query("SELECT endTime FROM Schedules WHERE scheduleId = :scheduleId")
    suspend fun getScheduleEndTime(scheduleId: Long): String?

    @Query("SELECT notes FROM Schedules WHERE scheduleId = :scheduleId")
    suspend fun getScheduleNotes(scheduleId: Long): String?

    @Query("SELECT * FROM Schedules WHERE usrId = :usrId ORDER BY dayOfWeek, startTime")
    suspend fun getScheduleForUser(usrId: Long): List<ScheduleEntity>

    @Query(
        """
        SELECT s.scheduleId, s.usrId, s.resourceId, s.scheduleName,
               s.dayOfWeek, s.startTime, s.endTime, s.notes,
               r.campusName, r.buildingName, r.buildingNumber,
               r.resourceType, r.resourceTag, r.availability,
               h.monday, h.tuesday, h.wednesday, h.thursday,
               h.friday, h.saturday, h.sunday
        FROM Schedules s
        INNER JOIN CampusResources r ON r.resourceId = s.resourceId
        LEFT JOIN ResourceHours h ON h.resourceId = r.resourceId
        WHERE s.usrId = :usrId
        ORDER BY s.dayOfWeek, s.startTime
        """
    )
    suspend fun getScheduleWithResources(usrId: Long): List<ScheduleWithResource>

    @Query("DELETE FROM Schedules WHERE scheduleId = :scheduleId AND usrId = :usrId")
    suspend fun deleteSchedule(scheduleId: Long, usrId: Long): Int
}
