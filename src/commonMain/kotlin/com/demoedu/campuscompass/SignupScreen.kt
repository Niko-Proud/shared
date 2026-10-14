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
import com.demoedu.campuscompass.data.UserEntity
import com.demoedu.campuscompass.data.createInMemoryCampusDatabase
import kotlinx.coroutines.launch

/** Creates a user account and relies on the database index to enforce unique usernames. */
@Composable
internal fun SignupScreen(
    database: CampusDatabase,
    onSignedIn: (Long) -> Unit,
    onBackToLogin: () -> Unit,
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
        Text("Create an account", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Create an account to save your campus schedule.")
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it; message = null },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("signup_username"),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; message = null },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("signup_password"),
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                scope.launch {
                    val cleanUsername = username.trim()
                    when {
                        cleanUsername.isEmpty() || password.isEmpty() -> {
                            message = "Enter both a username and password."
                        }
                        database.userDao().findUser(cleanUsername) != null -> {
                            message = "That username is already in use."
                        }
                        else -> {
                            val userId = database.userDao().insertUser(
                                UserEntity(username = cleanUsername, password = password),
                            )
                            onSignedIn(userId)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("signup_submit"),
        ) { Text("Create account") }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onBackToLogin,
            modifier = Modifier.fillMaxWidth().testTag("signup_back_to_login"),
        ) {
            Text("Back to login")
        }
        message?.let {
            Spacer(Modifier.height(16.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignupScreenPreview() {
    val database = remember { createInMemoryCampusDatabase() }
    MaterialTheme {
        SignupScreen(database = database, onSignedIn = {}, onBackToLogin = {})
    }
}
