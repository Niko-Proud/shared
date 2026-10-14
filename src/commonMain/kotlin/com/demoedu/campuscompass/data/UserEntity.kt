package com.demoedu.campuscompass.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "Users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val usrId: Long = 0,
    val username: String,
    // This is intentionally plain text for local test data only. Hash passwords before production use.
    val password: String,
)
