package com.demoedu.campuscompass

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.demoedu.campuscompass.data.CampusDatabase
import com.demoedu.campuscompass.data.LoginSessionPersistence
import com.demoedu.campuscompass.data.ensureTestUser
import kotlinx.coroutines.launch

/** Provides the shared app shell, restores the saved login, and switches between app views. */
@Composable
fun App(
    database: CampusDatabase,
    loginSessionStore: LoginSessionPersistence? = null,
) {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            var signedInUserId by remember { mutableStateOf<Long?>(null) }
            var sessionLoaded by remember { mutableStateOf(loginSessionStore == null) }
            val scope = rememberCoroutineScope()

            // The development account is added only when it does not already exist.
            LaunchedEffect(database) { ensureTestUser(database) }

            // Waiting for the first preference value prevents a saved login from flashing past.
            LaunchedEffect(loginSessionStore) {
                if (loginSessionStore == null) {
                    sessionLoaded = true
                } else {
                    loginSessionStore.signedInUserId.collect { savedUserId ->
                        signedInUserId = savedUserId
                        sessionLoaded = true
                    }
                }
            }

            when {
                !sessionLoaded -> Text("Loading your session...")
                signedInUserId == null -> AuthScreen(
                    database = database,
                    onSignedIn = { userId ->
                        signedInUserId = userId
                        scope.launch { loginSessionStore?.saveSignedInUser(userId) }
                    },
                )
                else -> ResourceScreen(
                    database = database,
                    userId = signedInUserId!!,
                    onLogout = {
                        signedInUserId = null
                        scope.launch { loginSessionStore?.clearSignedInUser() }
                    },
                )
            }
        }
    }
}
