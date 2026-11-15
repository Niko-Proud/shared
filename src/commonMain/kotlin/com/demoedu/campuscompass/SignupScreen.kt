package com.demoedu.campuscompass

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Button
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
import com.demoedu.campuscompass.data.UserEntity
import campuscompass.shared.generated.resources.Res
import campuscompass.shared.generated.resources.logo
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

/** Creates a user account and relies on the Users unique index for duplicate protection. */
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

    // Validation gives immediate feedback; the database remains the final uniqueness authority.
    Column(Modifier.fillMaxSize()) {
        // Keep the form inset for readable controls, while leaving the original wide logo
        // outside the weighted content so it reaches both lower screen edges.
        Column(
            Modifier.weight(1f).fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Create an account")
            OutlinedTextField(
                value = username,
                onValueChange = { username = it; message = null },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth().testTag("signup_username"),
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; message = null },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().testTag("signup_password"),
            )
            Button(
                onClick = {
                    scope.launch {
                        val cleanUsername = username.trim()
                        when {
                            cleanUsername.isEmpty() || password.isEmpty() -> message = "Enter both a username and password."
                            database.userDao().findUser(cleanUsername) != null -> message = "That username is already in use."
                            else -> onSignedIn(database.userDao().insertUser(UserEntity(username = cleanUsername, password = password)))
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("signup_submit"),
            ) { Text("Create account") }
            Text(message.orEmpty())
            Button(onClick = onBackToLogin) { Text("Back to login") }
        }
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = "Campus Compass logo",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
