package com.demoedu.campuscompass.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

/** Reads the campus-wide maps that help students orient themselves before choosing a service. */
@Dao
interface CampusMapDao {
    /** Adds a map record; callers use the campus/type pair as its logical identity. */
    @Insert
    suspend fun insertMap(map: CampusMapEntity): Long

    /** Replaces metadata or the local image name without creating a duplicate map. */
    @Update
    suspend fun updateMap(map: CampusMapEntity)

    @Query("SELECT mapId FROM CampusMaps WHERE campusName = :campusName AND mapType = :mapType")
    suspend fun getMapId(campusName: String, mapType: String): Long?

    /** Observes the complete map catalog for administration or future map-management screens. */
    @Query("SELECT * FROM CampusMaps ORDER BY campusName, mapName")
    fun observeMaps(): Flow<List<CampusMapEntity>>

    /** Returns only maps belonging to the selected campus; this is the UI's campus boundary. */
    @Query("SELECT * FROM CampusMaps WHERE campusName = :campusName ORDER BY mapName")
    suspend fun getMapsAtCampus(campusName: String): List<CampusMapEntity>
}
