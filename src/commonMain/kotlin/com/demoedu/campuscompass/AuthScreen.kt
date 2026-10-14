package com.demoedu.campuscompass

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.demoedu.campuscompass.data.CampusDatabase

/** Chooses between the standalone login and account-creation views. */
@Composable
internal fun AuthScreen(database: CampusDatabase, onSignedIn: (Long) -> Unit) {
    var showSignup by remember { mutableStateOf(false) }

    if (showSignup) {
        SignupScreen(
            database = database,
            onSignedIn = onSignedIn,
            onBackToLogin = { showSignup = false },
        )
    } else {
        LoginScreen(
            database = database,
            onSignedIn = onSignedIn,
            onCreateAccount = { showSignup = true },
        )
    }
}
