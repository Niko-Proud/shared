package com.demoedu.campuscompass

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.demoedu.campuscompass.data.CampusResourceEntity
import com.demoedu.campuscompass.data.ResourceHoursEntity

/** Presents the selected building's identity, resource status, and day-by-day hours. */
@Composable
internal fun ResourceCard(resource: CampusResourceEntity, hours: ResourceHoursEntity? = null) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(resource.resourceTag, style = MaterialTheme.typography.titleMedium)
            Text("${resource.buildingName} ${resource.buildingNumber} · ${resource.campusName}")
            Text("Type: ${resource.resourceType}")
            Text(if (resource.availability) "Available" else "Unavailable")
            if (hours != null) {
                Text("Hours", style = MaterialTheme.typography.titleSmall)
                Text("Monday: ${hours.monday}")
                Text("Tuesday: ${hours.tuesday}")
                Text("Wednesday: ${hours.wednesday}")
                Text("Thursday: ${hours.thursday}")
                Text("Friday: ${hours.friday}")
                Text("Saturday: ${hours.saturday}")
                Text("Sunday: ${hours.sunday}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ResourceCardPreview() {
    MaterialTheme {
        ResourceCard(
            CampusResourceEntity(
                campusName = "North Campus",
                buildingName = "Library",
                buildingNumber = "B-104",
                resourceType = "Study Room",
                resourceTag = "LIB-STUDY-104",
                availability = true,
            ),
            ResourceHoursEntity(
                resourceId = 0L,
                monday = "08:00-17:00",
                tuesday = "08:00-17:00",
                wednesday = "08:00-17:00",
                thursday = "08:00-20:00",
                friday = "08:00-15:00",
                saturday = "Closed",
                sunday = "Closed",
            ),
        )
    }
}
