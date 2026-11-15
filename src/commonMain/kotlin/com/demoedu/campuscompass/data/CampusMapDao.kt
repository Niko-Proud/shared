package com.demoedu.campuscompass.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

/** Reads the campus-wide maps that help students orient themselves before choosing a service. */
@Dao
interface CampusMapDao {
    @Insert
    suspend fun insertMap(map: CampusMapEntity): Long

    @Update
    suspend fun updateMap(map: CampusMapEntity)

    @Query("SELECT mapId FROM CampusMaps WHERE campusName = :campusName AND mapType = :mapType")
    suspend fun getMapId(campusName: String, mapType: String): Long?

    @Query("SELECT * FROM CampusMaps ORDER BY campusName, mapName")
    fun observeMaps(): Flow<List<CampusMapEntity>>

    @Query("SELECT * FROM CampusMaps WHERE campusName = :campusName ORDER BY mapName")
    suspend fun getMapsAtCampus(campusName: String): List<CampusMapEntity>
}
