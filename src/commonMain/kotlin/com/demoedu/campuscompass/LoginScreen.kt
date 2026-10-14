package com.demoedu.campuscompass

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.demoedu.campuscompass.data.CampusDatabase
import com.demoedu.campuscompass.data.createInMemoryCampusDatabase
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Campus Compass", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Log in to view campus resources.")
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it; message = null },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("login_username"),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; message = null },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("login_password"),
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                scope.launch {
                    val user = database.userDao().findUser(username.trim())
                    if (user != null && user.password == password) {
                        onSignedIn(user.usrId)
                    } else {
                        message = "Username or password is incorrect."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("login_submit"),
        ) { Text("Log in") }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onCreateAccount,
            modifier = Modifier.fillMaxWidth().testTag("login_create_account"),
        ) {
            Text("Create an account")
        }
        message?.let {
            Spacer(Modifier.height(16.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    val database = remember { createInMemoryCampusDatabase() }
    MaterialTheme {
        LoginScreen(database = database, onSignedIn = {}, onCreateAccount = {})
    }
}
