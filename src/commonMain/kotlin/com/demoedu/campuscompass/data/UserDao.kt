package com.demoedu.campuscompass.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query

@Dao
interface UserDao {
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
}
