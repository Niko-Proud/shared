package com.demoedu.campuscompass

import kotlinx.cinterop.ExperimentalForeignApi

import androidx.compose.ui.window.ComposeUIViewController
import com.demoedu.campuscompass.data.buildCampusDatabase
import com.demoedu.campuscompass.data.getCampusDatabaseBuilder
import com.demoedu.campuscompass.data.createLoginSessionStore
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/** Starts the iOS host with the shared database and persistent login store. */
@OptIn(ExperimentalForeignApi::class)
fun MainViewController() = ComposeUIViewController {
    // The document directory is shared by the database and preferences across launches.
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )?.path ?: error("Unable to locate the iOS documents directory")
    val database = buildCampusDatabase(getCampusDatabaseBuilder())
    val sessionStore = createLoginSessionStore("$documentDirectory/campus_compass.preferences_pb")
    App(database, sessionStore)
}
