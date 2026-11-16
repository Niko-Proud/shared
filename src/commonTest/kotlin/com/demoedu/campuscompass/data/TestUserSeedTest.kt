package com.demoedu.campuscompass.data

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** Confirms the development account is available, repairable, and not duplicated on relaunch. */
class TestUserSeedTest {
    @Test
    fun testUserIsInsertedOnlyOnce() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            ensureTestUser(database)
            ensureTestUser(database)

            val user = database.userDao().findUser(testUsername)
            assertEquals(testPassword, user?.password)
        } finally {
            database.close()
        }
    }

    @Test
    fun staleTestUserPasswordIsRefreshedWithoutCreatingAnotherUser() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            val existingId = database.userDao().insertUser(
                UserEntity(username = testUsername, password = "old-local-password"),
            )

            ensureTestUser(database)

            val user = database.userDao().findUser(testUsername)
            assertEquals(existingId, user?.usrId)
            assertEquals(testPassword, user?.password)
        } finally {
            database.close()
        }
    }
}
