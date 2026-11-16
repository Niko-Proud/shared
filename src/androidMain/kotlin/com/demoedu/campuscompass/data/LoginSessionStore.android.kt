package com.demoedu.campuscompass.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File

/** Creates the single Android preferences file used to restore the last login. */
actual fun createLoginSessionStore(filePath: String): LoginSessionStore {
    val dataStore = PreferenceDataStoreFactory.create { File(filePath) }
    return LoginSessionStore(dataStore)
}
