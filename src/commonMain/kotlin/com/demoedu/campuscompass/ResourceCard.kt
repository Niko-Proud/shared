package com.demoedu.campuscompass

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.demoedu.campuscompass.data.CampusResourceEntity
import com.demoedu.campuscompass.data.ResourceHoursEntity

/** Displays the building information users need when choosing a campus resource. */
@Composable
internal fun ResourceCard(resource: CampusResourceEntity, hours: ResourceHoursEntity?) {
    Card {
        Column {
            // These fields answer the user's basic “where is it and can I use it?” questions.
            Text(resource.buildingName)
            Text("Building number: ${resource.buildingNumber}")
            Text("Campus: ${resource.campusName}")
            Text("Resource type: ${resource.resourceType}")
            Text("Available: ${resource.availability}")
            hours?.let {
                // Hours are optional because a resource may be imported before its schedule.
                Text("Monday: ${it.monday}")
                Text("Tuesday: ${it.tuesday}")
                Text("Wednesday: ${it.wednesday}")
                Text("Thursday: ${it.thursday}")
                Text("Friday: ${it.friday}")
                Text("Saturday: ${it.saturday}")
                Text("Sunday: ${it.sunday}")
            }
        }
    }
}
