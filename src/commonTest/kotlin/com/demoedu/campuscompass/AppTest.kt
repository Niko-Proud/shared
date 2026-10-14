package com.demoedu.campuscompass

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.demoedu.campuscompass.data.UserEntity
import com.demoedu.campuscompass.data.createInMemoryCampusDatabase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

/** Confirms the app shell changes views after sign-in and returns to login on logout. */
class AppTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun appCanSignInAndLogOut() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            database.userDao().insertUser(UserEntity(username = "alex", password = "secret"))

            runComposeUiTest {
                setContent { App(database) }
                onNodeWithTag("login_username").performTextInput("alex")
                onNodeWithTag("login_password").performTextInput("secret")
                onNodeWithTag("login_submit").performClick()
                waitForIdle()
                onNodeWithText("Welcome, alex").assertIsDisplayed()

                onNodeWithTag("resource_logout").performClick()
                waitForIdle()
                onNodeWithText("Campus Compass").assertIsDisplayed()
            }
        } finally {
            database.close()
        }
    }
}
