package com.demoedu.campuscompass

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.demoedu.campuscompass.data.CampusDatabase

/** Coordinates the two authentication views without moving database work into the app shell. */
@Composable
internal fun AuthScreen(database: CampusDatabase, onSignedIn: (Long) -> Unit) {
    // Keeping this choice local lets the user return to login without losing app-level state.
    var showSignup by remember { mutableStateOf(false) }
    if (showSignup) {
        SignupScreen(database, onSignedIn, onBackToLogin = { showSignup = false })
    } else {
        LoginScreen(database, onSignedIn, onCreateAccount = { showSignup = true })
    }
}
