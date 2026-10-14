package com.demoedu.campuscompass.data

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/** Checks that each weekday keeps its own operating-hours value. */
class ResourceHoursDaoTest {
    @Test
    fun hoursStoreAndReturnEveryDayForTheRelatedResource() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            val resourceId = database.campusResourceDao().insertResource(
                CampusResourceEntity(
                    campusName = "North Campus",
                    buildingName = "Main Library",
                    buildingNumber = "LIB-100",
                    resourceType = "Study Space",
                    resourceTag = "library-study",
                ),
            )
            database.resourceHoursDao().insertHours(
                ResourceHoursEntity(
                    resourceId = resourceId,
                    monday = "08:00-20:00",
                    tuesday = "08:00-20:00",
                    wednesday = "08:00-20:00",
                    thursday = "08:00-20:00",
                    friday = "08:00-18:00",
                    saturday = "10:00-16:00",
                    sunday = "Closed",
                ),
            )

            val hours = assertNotNull(database.resourceHoursDao().getHours(resourceId))
            assertEquals(resourceId, database.resourceHoursDao().getHoursResourceId(resourceId))
            assertEquals("08:00-20:00", database.resourceHoursDao().getMondayHours(resourceId))
            assertEquals("08:00-20:00", database.resourceHoursDao().getTuesdayHours(resourceId))
            assertEquals("08:00-20:00", database.resourceHoursDao().getWednesdayHours(resourceId))
            assertEquals("08:00-20:00", database.resourceHoursDao().getThursdayHours(resourceId))
            assertEquals("08:00-18:00", database.resourceHoursDao().getFridayHours(resourceId))
            assertEquals("10:00-16:00", database.resourceHoursDao().getSaturdayHours(resourceId))
            assertEquals("Closed", database.resourceHoursDao().getSundayHours(resourceId))
            assertEquals("Closed", hours.sunday)
        } finally {
            database.close()
        }
    }
}
