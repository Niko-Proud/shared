package com.demoedu.campuscompass

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.demoedu.campuscompass.data.CampusDatabase

/** Provides the shared app shell and switches between signed-out and signed-in content. */
@Composable
fun App(database: CampusDatabase) {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            var signedInUserId by remember { mutableStateOf<Long?>(null) }
            if (signedInUserId == null) {
                AuthScreen(
                    database = database,
                    onSignedIn = { signedInUserId = it },
                )
            } else {
                ResourceScreen(
                    database = database,
                    userId = signedInUserId!!,
                    onLogout = { signedInUserId = null },
                )
            }
        }
    }
}
