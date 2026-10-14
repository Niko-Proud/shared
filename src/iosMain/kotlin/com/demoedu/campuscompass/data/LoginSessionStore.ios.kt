package com.demoedu.campuscompass.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath

/** Creates the iOS preferences file alongside the app's persistent database. */
actual fun createLoginSessionStore(filePath: String): LoginSessionStore {
    val dataStore = PreferenceDataStoreFactory.createWithPath { filePath.toPath() }
    return LoginSessionStore(dataStore)
}
