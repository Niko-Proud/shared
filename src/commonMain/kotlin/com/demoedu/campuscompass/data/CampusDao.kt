package com.demoedu.campuscompass.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CampusDao {
    // Users: full-row inserts, bulk inserts, column reads, and updates.
    @Insert
    suspend fun insertUser(user: UserEntity): Long

    @Insert
    suspend fun insertUsers(users: List<UserEntity>): List<Long>

    @Query("SELECT * FROM Users WHERE username = :username LIMIT 1")
    suspend fun findUser(username: String): UserEntity?

    @Query("SELECT usrId FROM Users WHERE usrId = :usrId")
    suspend fun getUserId(usrId: Long): Long?

    @Query("SELECT username FROM Users WHERE usrId = :usrId")
    suspend fun getUsername(usrId: Long): String?

    @Query("SELECT password FROM Users WHERE usrId = :usrId")
    suspend fun getPassword(usrId: Long): String?

    @Query("UPDATE Users SET username = :username WHERE usrId = :usrId")
    suspend fun updateUsername(usrId: Long, username: String): Int

    @Query("UPDATE Users SET password = :password WHERE usrId = :usrId")
    suspend fun updatePassword(usrId: Long, password: String): Int

    // Campus resources: full-row inserts, column reads, tags, and campus filtering.
    @Insert
    suspend fun insertResource(resource: CampusResourceEntity): Long

    @Query("SELECT resourceId FROM CampusResources WHERE resourceId = :resourceId")
    suspend fun getResourceId(resourceId: Long): Long?

    @Query("SELECT campusName FROM CampusResources WHERE resourceId = :resourceId")
    suspend fun getCampusName(resourceId: Long): String?

    @Query("SELECT buildingName FROM CampusResources WHERE resourceId = :resourceId")
    suspend fun getBuildingName(resourceId: Long): String?

    @Query("SELECT buildingNumber FROM CampusResources WHERE resourceId = :resourceId")
    suspend fun getBuildingNumber(resourceId: Long): String?

    @Query("SELECT resourceType FROM CampusResources WHERE resourceId = :resourceId")
    suspend fun getResourceType(resourceId: Long): String?

    @Query("SELECT resourceTag FROM CampusResources WHERE resourceId = :resourceId")
    suspend fun getResourceTag(resourceId: Long): String?

    @Query("SELECT availability FROM CampusResources WHERE resourceId = :resourceId")
    suspend fun getAvailability(resourceId: Long): Boolean?

    @Query("SELECT DISTINCT resourceTag FROM CampusResources ORDER BY resourceTag")
    suspend fun getUniqueResourceTags(): List<String>

    @Query("SELECT * FROM CampusResources WHERE campusName = :campusName ORDER BY buildingName")
    suspend fun getResourcesAtCampus(campusName: String): List<CampusResourceEntity>

    @Query("SELECT * FROM CampusResources ORDER BY campusName, buildingName")
    fun observeResources(): Flow<List<CampusResourceEntity>>

    @Query("SELECT hoursOfOperation FROM CampusResources WHERE resourceId = :resourceId")
    suspend fun getHoursOfOperation(resourceId: Long): List<String>?

    // Schedules: each row belongs to one user and references one campus resource.
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
               r.resourceType, r.resourceTag, r.availability, r.hoursOfOperation
        FROM Schedules s
        INNER JOIN CampusResources r ON r.resourceId = s.resourceId
        WHERE s.usrId = :usrId
        ORDER BY s.dayOfWeek, s.startTime
        """
    )
    suspend fun getScheduleWithResources(usrId: Long): List<ScheduleWithResource>

    @Delete
    suspend fun deleteResource(resource: CampusResourceEntity)

    @Query("DELETE FROM Schedules WHERE scheduleId = :scheduleId AND usrId = :usrId")
    suspend fun deleteSchedule(scheduleId: Long, usrId: Long): Int
}
