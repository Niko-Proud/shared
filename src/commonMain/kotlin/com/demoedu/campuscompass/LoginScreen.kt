package com.demoedu.campuscompass

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.demoedu.campuscompass.data.CampusDatabase
import kotlinx.coroutines.launch

/** Authenticates an existing user and sends the user ID back to the app shell. */
@Composable
internal fun LoginScreen(
    database: CampusDatabase,
    onSignedIn: (Long) -> Unit,
    onCreateAccount: () -> Unit,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // The screen owns temporary form state; only a successful lookup leaves this view.
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Campus Compass", style = MaterialTheme.typography.headlineMedium)
        Text("Log in to view campus resources.")
        OutlinedTextField(
            value = username,
            onValueChange = { username = it; message = null },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("login_username"),
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; message = null },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("login_password"),
        )
        Button(
            onClick = {
                scope.launch {
                    // Usernames are trimmed for lookup, while the password is compared exactly.
                    val user = database.userDao().findUser(username.trim())
                    if (user != null && user.password == password) onSignedIn(user.usrId)
                    else message = "Username or password is incorrect."
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("login_submit"),
        ) { Text("Log in") }
        Text(message.orEmpty(), color = MaterialTheme.colorScheme.error)
        Button(onClick = onCreateAccount) { Text("Create an account") }
    }
}
