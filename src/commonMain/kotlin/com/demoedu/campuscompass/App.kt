package com.demoedu.campuscompass

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.demoedu.campuscompass.data.CampusDatabase
import com.demoedu.campuscompass.data.LoginSessionPersistence
import com.demoedu.campuscompass.data.ensureCampusResources
import com.demoedu.campuscompass.data.ensureCampusMaps
import com.demoedu.campuscompass.data.ensureCabrilloResources
import com.demoedu.campuscompass.data.ensureGavilanResources
import com.demoedu.campuscompass.data.ensureHartnellResources
import com.demoedu.campuscompass.data.ensureMpcResources
import com.demoedu.campuscompass.data.ensureTestUser
import com.demoedu.campuscompass.data.ensureUscResources
import campuscompass.shared.generated.resources.Res
import campuscompass.shared.generated.resources.campus_compass_banner
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

/** Provides the shared app shell, restores the saved login, and switches between app views. */
@Composable
fun App(
    database: CampusDatabase,
    loginSessionStore: LoginSessionPersistence? = null,
) {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                CampusCompassBanner()
                // Keep the banner in the shell rather than in each screen, so login, signup,
                // and the resource browser cannot drift into different branded layouts.
                Box(modifier = Modifier.fillMaxSize()) {
                    var signedInUserId by remember { mutableStateOf<Long?>(null) }
                    var sessionLoaded by remember { mutableStateOf(loginSessionStore == null) }
                    val scope = rememberCoroutineScope()

                    // These seeds make the local demo useful immediately while preserving the
                    // campus filter: each institution writes only rows carrying its own campus name.
                    LaunchedEffect(database) {
                        ensureTestUser(database)
                        ensureCampusResources(database)
                        ensureMpcResources(database)
                        ensureCabrilloResources(database)
                        ensureGavilanResources(database)
                        ensureHartnellResources(database)
                        ensureUscResources(database)
                        ensureCampusMaps(database)
                    }

                    // Waiting for the first preference value prevents a saved login from flashing past.
                    LaunchedEffect(loginSessionStore) {
                        if (loginSessionStore == null) {
                            sessionLoaded = true
                        } else {
                            loginSessionStore.signedInUserId.collect { savedUserId ->
                                signedInUserId = savedUserId
                                sessionLoaded = true
                            }
                        }
                    }

                    when {
                        !sessionLoaded -> Text("Loading your session...")
                        signedInUserId == null -> AuthScreen(
                            database = database,
                            onSignedIn = { userId ->
                                signedInUserId = userId
                                scope.launch { loginSessionStore?.saveSignedInUser(userId) }
                            },
                        )
                        else -> ResourceScreen(
                            database = database,
                            userId = signedInUserId!!,
                            onLogout = {
                                signedInUserId = null
                                scope.launch { loginSessionStore?.clearSignedInUser() }
                            },
                        )
                    }
                }
            }
        }
    }
}

/** Shared brand header shown above authentication and resource views. */
@Composable
private fun CampusCompassBanner() {
    // This accessor is generated from composeResources; rebuild after changing the source asset
    // instead of editing shared/build/generated by hand. See the Compose Resources documentation:
    // https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-images-resources.html
    Image(
        painter = painterResource(Res.drawable.campus_compass_banner),
        contentDescription = "Campus Compass",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxWidth().height(96.dp),
    )
}
