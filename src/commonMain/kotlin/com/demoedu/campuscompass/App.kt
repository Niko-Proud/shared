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
import androidx.compose.foundation.layout.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.demoedu.campuscompass.data.CampusDatabase
import com.demoedu.campuscompass.data.CampusResourceEntity
import com.demoedu.campuscompass.data.UserEntity
import kotlinx.coroutines.launch

private enum class AuthMode { Login, CreateAccount }

@Composable
fun App(database: CampusDatabase) {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            var signedInUserId by remember { mutableStateOf<Long?>(null) }
            if (signedInUserId == null) {
                AuthScreen(database) { signedInUserId = it }
            } else {
                ResourceScreen(database, signedInUserId!!) { signedInUserId = null }
            }
        }
    }
}

@Composable
private fun AuthScreen(database: CampusDatabase, onSignedIn: (Long) -> Unit) {
    var mode by remember { mutableStateOf(AuthMode.Login) }
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
        Text(
            if (mode == AuthMode.Login) "Log in to view campus resources."
            else "Create an account to save your campus schedule.",
        )
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it; message = null },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; message = null },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
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
                        mode == AuthMode.Login -> {
                            val user = database.campusDao().findUser(cleanUsername)
                            if (user != null && user.password == password) onSignedIn(user.usrId)
                            else message = "Username or password is incorrect."
                        }
                        else -> {
                            if (database.campusDao().findUser(cleanUsername) != null) {
                                message = "That username is already in use."
                            } else {
                                val userId = database.campusDao().insertUser(
                                    UserEntity(username = cleanUsername, password = password),
                                )
                                onSignedIn(userId)
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (mode == AuthMode.Login) "Log in" else "Create account")
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = {
                mode = if (mode == AuthMode.Login) AuthMode.CreateAccount else AuthMode.Login
                message = null
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (mode == AuthMode.Login) "Create an account" else "Back to login")
        }
        message?.let {
            Spacer(Modifier.height(16.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun ResourceScreen(database: CampusDatabase, userId: Long, onLogout: () -> Unit) {
    val resources by database.campusDao().observeResources().collectAsState(initial = emptyList())
    var username by remember { mutableStateOf("User") }

    LaunchedEffect(userId) {
        username = database.campusDao().getUsername(userId) ?: "User"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text("Welcome, $username", style = MaterialTheme.typography.headlineSmall)
                Text("Campus resources")
            }
            OutlinedButton(onClick = onLogout) { Text("Log out") }
        }
        Spacer(Modifier.height(20.dp))
        if (resources.isEmpty()) {
            Text("No campus resources have been added yet.")
        } else {
            resources.forEach { resource ->
                ResourceCard(resource)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ResourceCard(resource: CampusResourceEntity) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(resource.resourceTag, style = MaterialTheme.typography.titleMedium)
            Text("${resource.buildingName} ${resource.buildingNumber} · ${resource.campusName}")
            Text("Type: ${resource.resourceType}")
            Text(if (resource.availability) "Available" else "Unavailable")
        }
    }
}
