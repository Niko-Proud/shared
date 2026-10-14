package com.demoedu.campuscompass.data

const val testUsername = "testuser"
const val testPassword = "testpassword"

/** Adds one repeatable development account so the login screen can be tested immediately. */
suspend fun ensureTestUser(database: CampusDatabase) {
    if (database.userDao().findUser(testUsername) == null) {
        database.userDao().insertUser(
            UserEntity(username = testUsername, password = testPassword),
        )
    }
}
