package com.demoedu.campuscompass

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.input.pointer.pointerInput
import com.demoedu.campuscompass.data.CampusDatabase
import com.demoedu.campuscompass.data.CampusMapEntity
import com.demoedu.campuscompass.data.CampusResourceEntity
import com.demoedu.campuscompass.data.ResourceHoursEntity
import campuscompass.shared.generated.resources.Res
import campuscompass.shared.generated.resources.campus_map_cabrillo_aptos
import campuscompass.shared.generated.resources.campus_map_csumb
import campuscompass.shared.generated.resources.campus_map_gavilan_gilroy
import campuscompass.shared.generated.resources.campus_map_hartnell_main
import campuscompass.shared.generated.resources.campus_map_mpc_marina
import campuscompass.shared.generated.resources.campus_map_mpc_monterey
import campuscompass.shared.generated.resources.campus_map_mpc_pstc
import campuscompass.shared.generated.resources.campus_map_ucsc
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

/**
 * Lets users narrow resources by campus or type, search for a building, and review
 * the matching structure details and operating hours.
 */
@Composable
internal fun ResourceScreen(database: CampusDatabase, userId: Long, onLogout: () -> Unit) {
    // Maps are intentionally a separate search mode. They are campus-level reference material,
    // not building resources, so keeping their results separate prevents map metadata from
    // appearing in the normal resource list before the user asks for it.
    val mapResourceType = "Maps"
    val dao = database.campusResourceDao()
    val campuses by dao.observeCampusNames().collectAsState(emptyList())
    val types by dao.observeResourceTypes().collectAsState(emptyList())
    val searchableTypes = remember(types) { (types + mapResourceType).distinct().sorted() }
    var campus by remember { mutableStateOf<String?>(null) }
    var type by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("User") }
    var results by remember { mutableStateOf<List<ResourceResult>>(emptyList()) }
    var mapResults by remember { mutableStateOf<List<CampusMapEntity>>(emptyList()) }
    // This flag distinguishes the initial screen from a legitimate search with zero matches.
    // Without it, an empty result list would look like a failed query immediately after login.
    var hasSearched by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // The header identifies the signed-in user while the rest of the screen remains resource-focused.
    LaunchedEffect(userId) { username = database.userDao().getUsername(userId) ?: "User" }
    Column(Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column { Text("Welcome, $username"); Text("Campus resources") }
            OutlinedButton(onClick = onLogout, modifier = Modifier.testTag("resource_logout")) { Text("Log out") }
        }
        // These dropdowns are populated from distinct database values, so the UI stays data-driven.
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterDropdown("Campus", campus, campuses, { campus = it }, Modifier.weight(1f).testTag("resource_campus_filter"))
            FilterDropdown("Resource type", type, searchableTypes, { type = it }, Modifier.weight(1f).testTag("resource_type_filter"))
        }
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search building name or number") },
            modifier = Modifier.fillMaxWidth().testTag("resource_building_search"),
        )
        // Building lookup is deliberately separate from the dropdown filters for direct searches.
        Button(onClick = {
            scope.launch {
                if (type == mapResourceType) {
                    // Maps live in their own table because they describe a campus, not a building.
                    // Requiring a campus keeps a map search from mixing locations together.
                    results = emptyList()
                    mapResults = campus?.let { database.campusMapDao().getMapsAtCampus(it) } ?: emptyList()
                } else {
                    // Do not query until the user asks; an empty query would otherwise render the
                    // entire multi-campus catalog immediately after sign-in.
                    mapResults = emptyList()
                    results = database.loadResourceResults(campus, type, query.trim())
                }
                hasSearched = true
            }
        }, modifier = Modifier.fillMaxWidth().testTag("resource_search_submit")) { Text("Find resources") }
        Spacer(Modifier.height(12.dp))
        if (hasSearched && type == mapResourceType) {
            if (campus == null) {
                Text("Select a campus to find its map.")
            } else if (mapResults.isEmpty()) {
                Text("No maps were found for this campus.")
            } else {
                mapResults.forEach { CampusMapCard(it) }
            }
        } else {
            if (hasSearched && results.isEmpty()) Text("No matching resources were found.")
            results.forEach { ResourceCard(it.resource, it.hours) }
        }

    }
}

/** Displays map metadata only after the user explicitly searches for the Maps resource type. */
@Composable
private fun CampusMapCard(map: CampusMapEntity) {
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(map.mapName)
        Text(map.description)
        // The database stores a stable resource name rather than a file path. MapImage resolves
        // that name through generated Compose accessors and safely ignores unknown values.
        map.imageResource?.let { MapImage(it, map.mapName) }
        Text("Official map: ${map.officialUrl}")
    }
}

/**
 * Resolves the database's stable image name to a generated Compose resource accessor.
 * Keeping this mapping explicit prevents a malformed database value from becoming a file path.
 */
@Composable
private fun MapImage(imageResource: String, description: String) {
    val painter = when (imageResource) {
        "campus_map_csumb" -> painterResource(Res.drawable.campus_map_csumb)
        "campus_map_mpc_monterey" -> painterResource(Res.drawable.campus_map_mpc_monterey)
        "campus_map_mpc_marina" -> painterResource(Res.drawable.campus_map_mpc_marina)
        "campus_map_mpc_pstc" -> painterResource(Res.drawable.campus_map_mpc_pstc)
        "campus_map_cabrillo_aptos" -> painterResource(Res.drawable.campus_map_cabrillo_aptos)
        "campus_map_gavilan_gilroy" -> painterResource(Res.drawable.campus_map_gavilan_gilroy)
        "campus_map_hartnell_main" -> painterResource(Res.drawable.campus_map_hartnell_main)
        "campus_map_ucsc" -> painterResource(Res.drawable.campus_map_ucsc)
        else -> null
    }
    if (painter == null) return

    // Scale and translation belong to this map card, so each displayed campus map can be explored
    // independently instead of one map's zoom level affecting every result on the screen.
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { scale = (scale + 0.5f).coerceAtMost(5f) },
                modifier = Modifier.testTag("map_zoom_in"),
            ) { Text("+") }
            OutlinedButton(
                onClick = {
                    scale = (scale - 0.5f).coerceAtLeast(1f)
                    if (scale == 1f) {
                        offsetX = 0f
                        offsetY = 0f
                    }
                },
                modifier = Modifier.testTag("map_zoom_out"),
            ) { Text("−") }
            OutlinedButton(
                onClick = {
                    scale = 1f
                    offsetX = 0f
                    offsetY = 0f
                },
                modifier = Modifier.testTag("map_zoom_reset"),
            ) { Text("Reset") }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .clip(RoundedCornerShape(8.dp))
                .pointerInput(Unit) {
                    // This handles touch pinch/pan and mouse drag gestures. The visible buttons
                    // provide an equally discoverable zoom path for desktop and emulator users.
                    // Translation is deliberately left in pixels: graphicsLayer applies it after
                    // layout, allowing the image to move within the fixed map viewport.
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(1f, 5f)
                        offsetX += pan.x
                        offsetY += pan.y
                        if (scale == 1f) {
                            offsetX = 0f
                            offsetY = 0f
                        }
                    }
                },
        ) {
            Image(
                painter = painter,
                contentDescription = description,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // The image is kept inside a clipped viewport so zooming enlarges the map
                        // without changing the surrounding resource card's layout height.
                        scaleX = scale
                        scaleY = scale
                        translationX = offsetX
                        translationY = offsetY
                    },
            )
        }
    }
}

private data class ResourceResult(val resource: CampusResourceEntity, val hours: ResourceHoursEntity?)

private suspend fun CampusDatabase.loadResourceResults(campus: String?, type: String?, query: String): List<ResourceResult> =
    // The DAO performs campus/type/name filtering in SQL. The hours lookup is kept beside each
    // resource so cards can render complete building details without a second UI query.
    campusResourceDao().searchResources(campus, type, query).map { resource ->
        ResourceResult(resource, resourceHoursDao().getHours(resource.resourceId))
    }

@Composable
private fun FilterDropdown(
    label: String,
    selected: String?,
    options: List<String>,
    onSelected: (String?) -> Unit,
    modifier: Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    // The menu changes only the selected filter; the parent performs the next database query.
    Column(modifier) {
        OutlinedButton(onClick = { expanded = true }, Modifier.fillMaxWidth()) { Text(selected ?: "All $label") }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("All $label") }, onClick = { onSelected(null); expanded = false })
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onSelected(option); expanded = false })
            }
        }
    }
}
