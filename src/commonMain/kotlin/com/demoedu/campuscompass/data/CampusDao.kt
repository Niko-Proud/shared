package com.demoedu.campuscompass.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CampusDao {
    @Insert
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM Users WHERE username = :username LIMIT 1")
    suspend fun findUser(username: String): UserEntity?

    @Insert
    suspend fun insertResource(resource: CampusResourceEntity): Long

    @Query("SELECT * FROM CampusResources ORDER BY campusName, buildingName")
    fun observeResources(): Flow<List<CampusResourceEntity>>

    @Query("SELECT * FROM ResourceHours WHERE resourceId = :resourceId ORDER BY dayOfWeek")
    suspend fun getHours(resourceId: Long): List<ResourceHoursEntity>

    @Insert
    suspend fun insertHours(hours: ResourceHoursEntity): Long

    @Delete
    suspend fun deleteResource(resource: CampusResourceEntity)
}
