package com.demoedu.campuscompass.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Keeps the small amount of session state needed to restore the last signed-in user. */
interface LoginSessionPersistence {
    val signedInUserId: Flow<Long?>

    suspend fun saveSignedInUser(userId: Long)

    suspend fun clearSignedInUser()
}

/** Stores only the user ID; credentials remain in Room and are never copied into preferences. */
class LoginSessionStore internal constructor(
    private val dataStore: DataStore<Preferences>,
) : LoginSessionPersistence {
    private companion object {
        val SignedInUserId = longPreferencesKey("signed_in_user_id")
    }

    override val signedInUserId: Flow<Long?> = dataStore.data.map { preferences ->
        preferences[SignedInUserId]
    }

    override suspend fun saveSignedInUser(userId: Long) {
        dataStore.edit { preferences -> preferences[SignedInUserId] = userId }
    }

    override suspend fun clearSignedInUser() {
        dataStore.edit { preferences -> preferences.remove(SignedInUserId) }
    }
}

/** Builds the platform file-backed store used by the Android and iOS app entry points. */
expect fun createLoginSessionStore(filePath: String): LoginSessionStore
