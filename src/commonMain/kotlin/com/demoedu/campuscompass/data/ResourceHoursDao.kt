package com.demoedu.campuscompass.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query

@Dao
interface ResourceHoursDao {
    @Insert
    suspend fun insertHours(hours: ResourceHoursEntity): Long

    @Query("SELECT * FROM ResourceHours WHERE resourceId = :resourceId")
    suspend fun getHours(resourceId: Long): ResourceHoursEntity?

    @Query("SELECT resourceId FROM ResourceHours WHERE resourceId = :resourceId")
    suspend fun getHoursResourceId(resourceId: Long): Long?

    @Query("SELECT monday FROM ResourceHours WHERE resourceId = :resourceId")
    suspend fun getMondayHours(resourceId: Long): String?

    @Query("SELECT tuesday FROM ResourceHours WHERE resourceId = :resourceId")
    suspend fun getTuesdayHours(resourceId: Long): String?

    @Query("SELECT wednesday FROM ResourceHours WHERE resourceId = :resourceId")
    suspend fun getWednesdayHours(resourceId: Long): String?

    @Query("SELECT thursday FROM ResourceHours WHERE resourceId = :resourceId")
    suspend fun getThursdayHours(resourceId: Long): String?

    @Query("SELECT friday FROM ResourceHours WHERE resourceId = :resourceId")
    suspend fun getFridayHours(resourceId: Long): String?

    @Query("SELECT saturday FROM ResourceHours WHERE resourceId = :resourceId")
    suspend fun getSaturdayHours(resourceId: Long): String?

    @Query("SELECT sunday FROM ResourceHours WHERE resourceId = :resourceId")
    suspend fun getSundayHours(resourceId: Long): String?
}
