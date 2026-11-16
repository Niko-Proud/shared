package com.demoedu.campuscompass.data

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

/**
 * Stores a searchable name used for a resource without replacing the institution's formal name.
 * One resource may have several aliases, so aliases belong in rows rather than a delimited column.
 */
@Entity(
    tableName = "CampusResourceAliases",
    indices = [
        Index(value = ["resourceId", "alias"], unique = true),
        Index(value = ["alias"]),
    ],
)
data class CampusResourceAliasEntity(
    @PrimaryKey(autoGenerate = true)
    val aliasId: Long = 0,
    val resourceId: Long,
    val alias: String,
)
