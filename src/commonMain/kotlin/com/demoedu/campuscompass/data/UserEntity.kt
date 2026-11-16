package com.demoedu.campuscompass.data

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

/** Stores the test app's login records; usernames are unique at the database level. */
@Entity(
    tableName = "Users",
    indices = [Index(value = ["username"], unique = true)],
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val usrId: Long = 0,
    val username: String, // Login name and unique account identifier.
    // This stores test credentials; production authentication should store a password hash.
    val password: String, // Test-only credential; production should store a password hash.
)
