package com.demoedu.campuscompass

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.demoedu.campuscompass.data.CampusResourceEntity
import com.demoedu.campuscompass.data.ResourceHoursEntity
import com.demoedu.campuscompass.data.UserEntity
import com.demoedu.campuscompass.data.createInMemoryCampusDatabase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

/** Confirms the resource view renders database details and filters its results. */
class ResourceScreenTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun resourceViewDisplaysBuildingDetailsHoursAndSearchResults() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            val userId = database.userDao().insertUser(UserEntity(username = "alex", password = "secret"))
            val libraryId = database.campusResourceDao().insertResource(
                CampusResourceEntity(
                    campusName = "North Campus",
                    buildingName = "Main Library",
                    buildingNumber = "LIB-100",
                    resourceType = "Study Space",
                    resourceTag = "library-study",
                ),
            )
            database.campusResourceDao().insertResource(
                CampusResourceEntity(
                    campusName = "North Campus",
                    buildingName = "Science Hall",
                    buildingNumber = "SCI-200",
                    resourceType = "Lab",
                    resourceTag = "science-lab",
                ),
            )
            database.resourceHoursDao().insertHours(
                ResourceHoursEntity(libraryId, "08:00-20:00", "08:00-20:00", "08:00-20:00", "08:00-20:00", "08:00-18:00", "10:00-16:00", "Closed"),
            )

            runComposeUiTest {
                setContent { MaterialTheme { ResourceScreen(database, userId, onLogout = {}) } }
                waitForIdle()
                onNodeWithText("Welcome, alex").assertIsDisplayed()
                onNodeWithText("Main Library").assertIsDisplayed()
                onNodeWithText("LIB-100").assertIsDisplayed()
                onNodeWithText("Monday: 08:00-20:00").assertIsDisplayed()

                onNodeWithTag("resource_campus_filter").performClick()
                onNodeWithText("North Campus").assertIsDisplayed()
                onNodeWithText("North Campus").performClick()

                onNodeWithTag("resource_building_search").performTextInput("Science")
                onNodeWithTag("resource_search_submit").performClick()
                waitForIdle()
                onNodeWithText("Science Hall").assertIsDisplayed()
            }
        } finally {
            database.close()
        }
    }
}
