package com.demoedu.campuscompass

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.demoedu.campuscompass.data.CampusDatabase
import com.demoedu.campuscompass.data.CampusResourceEntity
import com.demoedu.campuscompass.data.ResourceHoursEntity
import kotlinx.coroutines.launch

/**
 * Lets users narrow resources by campus or type, search for a building, and review
 * the matching structure details and operating hours.
 */
@Composable
internal fun ResourceScreen(database: CampusDatabase, userId: Long, onLogout: () -> Unit) {
    val dao = database.campusResourceDao()
    val campuses by dao.observeCampusNames().collectAsState(emptyList())
    val types by dao.observeResourceTypes().collectAsState(emptyList())
    var campus by remember { mutableStateOf<String?>(null) }
    var type by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("User") }
    var results by remember { mutableStateOf<List<ResourceResult>>(emptyList()) }
    val scope = rememberCoroutineScope()

    // The header identifies the signed-in user while the rest of the screen remains resource-focused.
    LaunchedEffect(userId) { username = database.userDao().getUsername(userId) ?: "User" }
    // Show all resources on entry; the controls apply narrower queries on demand.
    LaunchedEffect(Unit) { results = database.loadResourceResults(null, null, "") }

    Column(Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column { Text("Welcome, $username"); Text("Campus resources") }
            OutlinedButton(onClick = onLogout, modifier = Modifier.testTag("resource_logout")) { Text("Log out") }
        }
        // These dropdowns are populated from distinct database values, so the UI stays data-driven.
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterDropdown("Campus", campus, campuses, { campus = it }, Modifier.weight(1f).testTag("resource_campus_filter"))
            FilterDropdown("Resource type", type, types, { type = it }, Modifier.weight(1f).testTag("resource_type_filter"))
        }
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search building name or number") },
            modifier = Modifier.fillMaxWidth().testTag("resource_building_search"),
        )
        // Building lookup is deliberately separate from the dropdown filters for direct searches.
        Button(onClick = {
            scope.launch { results = database.loadResourceResults(campus, type, query.trim()) }
        }, modifier = Modifier.fillMaxWidth().testTag("resource_search_submit")) { Text("Find resources") }
        Spacer(Modifier.height(12.dp))
        if (results.isEmpty()) Text("No matching resources were found.")
        results.forEach { ResourceCard(it.resource, it.hours) }
    }
}

private data class ResourceResult(val resource: CampusResourceEntity, val hours: ResourceHoursEntity?)

private suspend fun CampusDatabase.loadResourceResults(campus: String?, type: String?, query: String): List<ResourceResult> =
    // The hours lookup is kept beside each resource so cards can render complete building details.
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
