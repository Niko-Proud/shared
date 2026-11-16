package com.demoedu.campuscompass

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import com.demoedu.campuscompass.data.LoginSessionPersistence
import kotlin.test.Test
import kotlin.test.assertEquals

/** Checks the session contract used by the DataStore-backed production implementation. */
class LoginSessionPersistenceTest {
    @Test
    fun savingAndClearingTheSessionUpdatesTheRestoredUser() = runTest {
        val persistence = TestSessionPersistence()

        assertEquals(null, persistence.signedInUserId.first())
        persistence.saveSignedInUser(42L)
        assertEquals(42L, persistence.signedInUserId.first())
        persistence.clearSignedInUser()
        assertEquals(null, persistence.signedInUserId.first())
    }

    private class TestSessionPersistence : LoginSessionPersistence {
        private val state = MutableStateFlow<Long?>(null)

        override val signedInUserId: Flow<Long?> = state

        override suspend fun saveSignedInUser(userId: Long) {
            state.value = userId
        }

        override suspend fun clearSignedInUser() {
            state.value = null
        }
    }
}
