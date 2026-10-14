package com.demoedu.campuscompass

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.demoedu.campuscompass.data.UserEntity
import com.demoedu.campuscompass.data.createInMemoryCampusDatabase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** Exercises the login and account-creation flows from the user’s perspective. */
class AuthScreenTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun loginAcceptsValidCredentialsAndReportsInvalidCredentials() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            val expectedId = database.userDao().insertUser(UserEntity(username = "alex", password = "secret"))
            var signedInId: Long? = null

            runComposeUiTest {
                setContent {
                    MaterialTheme {
                        LoginScreen(database, onSignedIn = { signedInId = it }, onCreateAccount = {})
                    }
                }
                onNodeWithTag("login_username").performTextInput("alex")
                onNodeWithTag("login_password").performTextInput("wrong")
                onNodeWithTag("login_submit").performClick()
                waitForIdle()
                onNodeWithText("Username or password is incorrect.").assertIsDisplayed()
                assertEquals(null, signedInId)

                onNodeWithTag("login_password").performTextInput("secret")
                onNodeWithTag("login_submit").performClick()
                waitForIdle()
                assertEquals(expectedId, signedInId)
            }
        } finally {
            database.close()
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun signupRejectsBlankAndDuplicateAccountsAndCreatesAValidAccount() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            database.userDao().insertUser(UserEntity(username = "taken", password = "secret"))
            var signedInId: Long? = null

            runComposeUiTest {
                setContent {
                    MaterialTheme {
                        SignupScreen(database, onSignedIn = { signedInId = it }, onBackToLogin = {})
                    }
                }
                onNodeWithTag("signup_submit").performClick()
                waitForIdle()
                onNodeWithText("Enter both a username and password.").assertIsDisplayed()

                onNodeWithTag("signup_username").performTextInput("taken")
                onNodeWithTag("signup_password").performTextInput("another")
                onNodeWithTag("signup_submit").performClick()
                waitForIdle()
                onNodeWithText("That username is already in use.").assertIsDisplayed()
                assertEquals(null, signedInId)

                onNodeWithTag("signup_username").performTextInput("-new")
                onNodeWithTag("signup_submit").performClick()
                waitForIdle()
                assertEquals("taken-new", database.userDao().findUser("taken-new")?.username)
                assertEquals(database.userDao().findUser("taken-new")?.usrId, signedInId)
            }
        } finally {
            database.close()
        }
    }
}
