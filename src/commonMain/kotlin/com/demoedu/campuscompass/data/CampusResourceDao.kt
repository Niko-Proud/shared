package com.demoedu.campuscompass.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CampusResourceDao {
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

    @Delete
    suspend fun deleteResource(resource: CampusResourceEntity)
}
