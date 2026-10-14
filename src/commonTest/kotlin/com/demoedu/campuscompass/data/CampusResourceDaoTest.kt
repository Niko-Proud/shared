package com.demoedu.campuscompass.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Checks resource details, filter values, campus filtering, and building search. */
class CampusResourceDaoTest {
    @Test
    fun resourcesExposeTheirColumnsAndDistinctFilterValues() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            val dao = database.campusResourceDao()
            val libraryId = dao.insertResource(
                CampusResourceEntity(
                    campusName = "North Campus",
                    buildingName = "Main Library",
                    buildingNumber = "LIB-100",
                    resourceType = "Study Space",
                    resourceTag = "library-study",
                    availability = true,
                ),
            )
            dao.insertResource(
                CampusResourceEntity(
                    campusName = "North Campus",
                    buildingName = "Science Hall",
                    buildingNumber = "SCI-200",
                    resourceType = "Lab",
                    resourceTag = "science-lab",
                    availability = false,
                ),
            )
            dao.insertResource(
                CampusResourceEntity(
                    campusName = "South Campus",
                    buildingName = "Main Library Annex",
                    buildingNumber = "LIB-300",
                    resourceType = "Study Space",
                    resourceTag = "library-annex",
                ),
            )

            assertEquals(libraryId, dao.getResourceId(libraryId))
            assertEquals("North Campus", dao.getCampusName(libraryId))
            assertEquals("Main Library", dao.getBuildingName(libraryId))
            assertEquals("LIB-100", dao.getBuildingNumber(libraryId))
            assertEquals("Study Space", dao.getResourceType(libraryId))
            assertEquals("library-study", dao.getResourceTag(libraryId))
            assertTrue(dao.getAvailability(libraryId) == true)
            assertEquals(listOf("library-annex", "library-study", "science-lab"), dao.getUniqueResourceTags())
            assertEquals(2, dao.getResourcesAtCampus("North Campus").size)
            assertEquals(listOf("North Campus", "South Campus"), dao.observeCampusNames().first())
            assertEquals(listOf("Lab", "Study Space"), dao.observeResourceTypes().first())
        } finally {
            database.close()
        }
    }

    @Test
    fun resourceSearchSupportsFiltersAndReturnsAnIntentionalNoMatch() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            val dao = database.campusResourceDao()
            dao.insertResource(
                CampusResourceEntity(
                    campusName = "North Campus",
                    buildingName = "Main Library",
                    buildingNumber = "LIB-100",
                    resourceType = "Study Space",
                    resourceTag = "library-study",
                ),
            )
            dao.insertResource(
                CampusResourceEntity(
                    campusName = "North Campus",
                    buildingName = "Science Hall",
                    buildingNumber = "SCI-200",
                    resourceType = "Lab",
                    resourceTag = "science-lab",
                ),
            )

            assertEquals("Main Library", dao.searchResources(null, null, "LIB-100").single().buildingName)
            assertEquals("Science Hall", dao.searchResources("North Campus", "Lab", "").single().buildingName)
            assertTrue(dao.searchResources("South Campus", null, "").isEmpty())
            assertTrue(dao.searchResources(null, null, "does-not-exist").isEmpty())
        } finally {
            database.close()
        }
    }
}
