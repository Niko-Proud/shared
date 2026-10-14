package com.demoedu.campuscompass.data

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Checks account creation, lookup, editing, and database-enforced uniqueness. */
class UserDaoTest {
    @Test
    fun usersCanBeInsertedLookedUpUpdatedAndInsertedInBulk() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            val dao = database.userDao()
            val firstId = dao.insertUser(UserEntity(username = "alex", password = "old-pass"))
            val bulkIds = dao.insertUsers(
                listOf(
                    UserEntity(username = "sam", password = "sam-pass"),
                    UserEntity(username = "riley", password = "riley-pass"),
                ),
            )

            assertTrue(firstId > 0)
            assertEquals(2, bulkIds.size)
            assertEquals(firstId, dao.getUserId(firstId))
            assertEquals("alex", dao.getUsername(firstId))
            assertEquals("old-pass", dao.getPassword(firstId))
            assertEquals(firstId, dao.findUser("alex")?.usrId)

            assertEquals(1, dao.updateUsername(firstId, "alexander"))
            assertEquals(1, dao.updatePassword(firstId, "new-pass"))
            assertNull(dao.findUser("alex"))
            assertEquals(firstId, dao.findUser("alexander")?.usrId)
            assertEquals("new-pass", dao.getPassword(firstId))
        } finally {
            database.close()
        }
    }

    @Test
    fun duplicateUsernamesFailInsteadOfCreatingASecondAccount() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            val dao = database.userDao()
            dao.insertUser(UserEntity(username = "same-name", password = "one"))

            assertFails {
                dao.insertUser(UserEntity(username = "same-name", password = "two"))
            }
        } finally {
            database.close()
        }
    }
}
