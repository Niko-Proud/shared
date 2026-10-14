package com.demoedu.campuscompass

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.demoedu.campuscompass.data.CampusDatabase
import com.demoedu.campuscompass.data.CampusResourceEntity
import com.demoedu.campuscompass.data.ResourceHoursEntity
import com.demoedu.campuscompass.data.createInMemoryCampusDatabase
import kotlinx.coroutines.launch

/**
 * Lets users narrow resources by campus or type, search for a building, and review
 * the matching structure details and operating hours.
 */
@Composable
internal fun ResourceScreen(database: CampusDatabase, userId: Long, onLogout: () -> Unit) {
    val campusNames by database.campusResourceDao().observeCampusNames().collectAsState(initial = emptyList())
    val resourceTypes by database.campusResourceDao().observeResourceTypes().collectAsState(initial = emptyList())
    var username by remember { mutableStateOf("User") }
    var selectedCampus by remember { mutableStateOf<String?>(null) }
    var selectedResourceType by remember { mutableStateOf<String?>(null) }
    var buildingQuery by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<ResourceResult>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        username = database.userDao().getUsername(userId) ?: "User"
    }

    LaunchedEffect(Unit) {
        results = database.loadResourceResults(null, null, "")
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
            OutlinedButton(onClick = onLogout, modifier = Modifier.testTag("resource_logout")) {
                Text("Log out")
            }
        }
        Spacer(Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ResourceFilterDropdown(
                label = "Campus",
                selectedValue = selectedCampus,
                options = campusNames,
                onSelected = { selectedCampus = it },
                modifier = Modifier.weight(1f).testTag("resource_campus_filter"),
            )
            ResourceFilterDropdown(
                label = "Resource type",
                selectedValue = selectedResourceType,
                options = resourceTypes,
                onSelected = { selectedResourceType = it },
                modifier = Modifier.weight(1f).testTag("resource_type_filter"),
            )
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = buildingQuery,
            onValueChange = { buildingQuery = it },
            label = { Text("Search building name or number") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("resource_building_search"),
        )
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            scope.launch {
                results = database.loadResourceResults(
                    selectedCampus,
                    selectedResourceType,
                    buildingQuery.trim(),
                )
            }
        }, modifier = Modifier.fillMaxWidth().testTag("resource_search_submit")) {
            Text("Find resources")
        }
        Spacer(Modifier.height(20.dp))
        if (results.isEmpty()) {
            Text("No matching resources were found.")
        } else {
            results.forEach { result ->
                ResourceCard(result.resource, result.hours)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

private data class ResourceResult(
    val resource: CampusResourceEntity,
    val hours: ResourceHoursEntity?,
)

private suspend fun CampusDatabase.loadResourceResults(
    campusName: String?,
    resourceType: String?,
    buildingQuery: String,
): List<ResourceResult> {
    val resources = campusResourceDao().searchResources(campusName, resourceType, buildingQuery)
    return resources.map { resource ->
        ResourceResult(resource, resourceHoursDao().getHours(resource.resourceId))
    }
}

@Composable
private fun ResourceFilterDropdown(
    label: String,
    selectedValue: String?,
    options: List<String>,
    onSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(selectedValue ?: "All $label")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("All $label") },
                onClick = { onSelected(null); expanded = false },
            )
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onSelected(option); expanded = false },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ResourceScreenPreview() {
    val database = remember { createInMemoryCampusDatabase() }
    MaterialTheme { ResourceScreen(database = database, userId = 1L, onLogout = {}) }
}
