package com.demoedu.campuscompass

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.demoedu.campuscompass.data.CampusDatabase

/** Provides the shared app shell and switches between signed-out and signed-in content. */
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

            // This makes the development account available without replacing existing users.
            LaunchedEffect(database) { ensureTestUser(database) }

            // The first DataStore value decides whether the app opens at login or resources.
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

            if (!sessionLoaded) {
                androidx.compose.material3.Text("Loading your session…")
            } else if (signedInUserId == null) {
                AuthScreen(
                    database = database,
                    onSignedIn = {
                        signedInUserId = it
                        scope.launch { loginSessionStore?.saveSignedInUser(it) }
                    },
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
