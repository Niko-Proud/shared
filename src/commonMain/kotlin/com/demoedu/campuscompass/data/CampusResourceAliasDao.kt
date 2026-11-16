package com.demoedu.campuscompass.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query

/** Owns alias reads and writes so alias maintenance stays separate from resource metadata. */
@Dao
interface CampusResourceAliasDao {
    @Insert
    suspend fun insertAlias(alias: CampusResourceAliasEntity): Long

    /** Returns aliases in display order for a resource detail view or search suggestion. */
    @Query("SELECT * FROM CampusResourceAliases WHERE resourceId = :resourceId ORDER BY alias")
    suspend fun getAliases(resourceId: Long): List<CampusResourceAliasEntity>

    /** Replaces only the seed-owned aliases during an idempotent catalog refresh. */
    @Query("DELETE FROM CampusResourceAliases WHERE resourceId = :resourceId")
    suspend fun deleteAliasesForResource(resourceId: Long)
}
