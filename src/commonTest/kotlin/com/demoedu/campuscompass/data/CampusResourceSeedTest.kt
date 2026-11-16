package com.demoedu.campuscompass.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Protects the local catalog used by the demo app. These tests intentionally exercise the
 * same idempotent seed function that App.kt calls, because a second launch must refresh
 * metadata without creating duplicate buildings or hours rows.
 */
class CampusResourceSeedTest {
    @Test
    fun campusCatalogIsSeededWithMetadataAndHours() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            ensureCampusResources(database)

            val resources = database.campusResourceDao().observeResources().first()
            assertTrue(resources.size >= 74)

            val library = resources.single { it.buildingNumber == "508" }
            assertEquals("Library", library.resourceType)
            assertTrue(library.description.contains("Tanimura & Antle Family Memorial Library"))
            assertEquals("https://csumb.edu/library/", library.officialUrl)
            assertEquals(null, library.imageResource)

            val hours = database.resourceHoursDao().getHours(library.resourceId)
            assertNotNull(hours)
            assertEquals("8:00 AM–11:00 PM", hours.monday)
            assertEquals("2:00 PM–11:00 PM", hours.sunday)

            ensureCampusMaps(database)
            val maps = database.campusMapDao().observeMaps().first()
            assertEquals(7, maps.size)
            assertTrue(maps.any { it.campusName == "MPC Monterey Campus" })
            assertEquals("campus_map_csumb", maps.single { it.campusName == "CSUMB Main Campus" }.imageResource)
        } finally {
            database.close()
        }
    }

    @Test
    fun mpcCatalogIncludesMultipleLocationsAndPublishedServices() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            ensureMpcResources(database)

            val resources = database.campusResourceDao().observeResources().first()
            val mpcResources = resources.filter { it.campusName.startsWith("MPC ") }
            assertTrue(mpcResources.size >= 25)
            assertTrue(mpcResources.any { it.buildingName == "Library and Technology Center" && it.buildingNumber == "16" })
            assertTrue(mpcResources.any { it.buildingName == "Lobo Hub" && it.campusName == "MPC Marina Campus" })
            assertTrue(mpcResources.any { it.buildingName == "Student Health Services" && it.officialUrl == "https://mpc.edu/student-support/health-and-wellness/student-health-services/" })
        } finally {
            database.close()
        }
    }

    @Test
    fun campusQueriesDoNotReturnResourcesFromAnotherCampus() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            ensureCampusResources(database)
            ensureMpcResources(database)

            val mpcOnly = database.campusResourceDao().getResourcesAtCampus("MPC Monterey Campus")
            assertTrue(mpcOnly.isNotEmpty())
            assertTrue(mpcOnly.all { it.campusName == "MPC Monterey Campus" })
            assertTrue(mpcOnly.none { it.campusName == "CSUMB Main Campus" })

            val filtered = database.campusResourceDao().searchResources(
                campusName = "MPC Monterey Campus",
                resourceType = null,
                buildingQuery = "Library",
            )
            assertTrue(filtered.isNotEmpty())
            assertTrue(filtered.all { it.campusName == "MPC Monterey Campus" })
            assertTrue(filtered.all { it.buildingName.contains("Library") })
        } finally {
            database.close()
        }
    }

    @Test
    fun cabrilloCatalogIncludesAptosAndWatsonvilleResources() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            ensureCabrilloResources(database)
            ensureCampusMaps(database)

            val resources = database.campusResourceDao().observeResources().first()
            val cabrillo = resources.filter { it.campusName.startsWith("Cabrillo ") }
            assertTrue(cabrillo.size >= 25)
            assertTrue(cabrillo.any { it.buildingName == "Swenson Library" && it.buildingNumber == "1000" })
            assertEquals("https://www.cabrillo.edu/welcome-center/", cabrillo.single { it.buildingName == "Welcome Center" }.officialUrl)
            assertEquals(null, cabrillo.single { it.buildingName == "SAC West" }.officialUrl)
            assertTrue(cabrillo.any { it.buildingName == "Watsonville Center" && it.campusName == "Cabrillo Watsonville Center" })
            assertTrue(cabrillo.any { it.buildingName == "Student Health Services" && it.officialUrl == "https://www.cabrillo.edu/student-health-services/" })

            // Campus selection is part of the data contract: Aptos results must not leak
            // Watsonville resources into a student's search results.
            val aptosOnly = database.campusResourceDao().getResourcesAtCampus("Cabrillo Aptos Campus")
            assertTrue(aptosOnly.isNotEmpty())
            assertTrue(aptosOnly.all { it.campusName == "Cabrillo Aptos Campus" })
            assertTrue(aptosOnly.none { it.campusName == "Cabrillo Watsonville Center" })

            val maps = database.campusMapDao().observeMaps().first()
            assertTrue(maps.any { it.campusName == "Cabrillo Aptos Campus" })
            assertTrue(maps.any { it.campusName == "Cabrillo Watsonville Center" })
        } finally {
            database.close()
        }
    }

    @Test
    fun newerCampusCatalogsIncludeVerifiedLocationsAndStaySeparated() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            ensureGavilanResources(database)
            ensureHartnellResources(database)
            ensureUscResources(database)
            ensureCampusMaps(database)

            val resources = database.campusResourceDao().observeResources().first()
            assertTrue(resources.count { it.campusName == "Gavilan Gilroy Campus" } >= 10)
            assertTrue(resources.count { it.campusName.startsWith("Hartnell ") } >= 20)
            assertTrue(resources.count { it.campusName == "UC Santa Cruz Main Campus" } >= 12)
            assertTrue(resources.any { it.campusName == "Gavilan Gilroy Campus" && it.buildingName == "Library and Student Resource Center" })
            assertTrue(resources.any { it.campusName == "Hartnell Main Campus" && it.buildingName == "Panther Learning Lab" })
            assertTrue(resources.any { it.campusName == "UC Santa Cruz Main Campus" && it.buildingName == "Cowell Student Health Center" })

            val hartnellOnly = database.campusResourceDao().getResourcesAtCampus("Hartnell Main Campus")
            assertTrue(hartnellOnly.isNotEmpty())
            assertTrue(hartnellOnly.all { it.campusName == "Hartnell Main Campus" })
            assertTrue(hartnellOnly.none { it.campusName == "Gavilan Gilroy Campus" })

            val maps = database.campusMapDao().observeMaps().first()
            assertTrue(maps.any { it.campusName == "Gavilan Gilroy Campus" })
            assertTrue(maps.any { it.campusName == "Hartnell Main Campus" })
            assertTrue(maps.any { it.campusName == "UC Santa Cruz Main Campus" })
            assertTrue(maps.single { it.campusName == "Gavilan Gilroy Campus" }.imageResource != null)
            assertTrue(maps.single { it.campusName == "Hartnell Main Campus" }.imageResource != null)
            assertTrue(maps.single { it.campusName == "UC Santa Cruz Main Campus" }.imageResource != null)
        } finally {
            database.close()
        }
    }

    @Test
    fun reseedingUpdatesExistingMetadataWithoutDuplicatingRows() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            val existingId = database.campusResourceDao().insertResource(
                CampusResourceEntity(
                    campusName = "CSUMB Main Campus",
                    buildingName = "Old Library Label",
                    buildingNumber = "508",
                    resourceType = "Old Type",
                    resourceTag = "csumb-508-tanimura-antle-family-memorial-library",
                    description = "Stale description",
                ),
            )

            ensureCampusResources(database)
            ensureCampusResources(database)

            val resources = database.campusResourceDao().observeResources().first()
            val library = resources.single { it.resourceId == existingId }
            assertEquals("Tanimura & Antle Family Memorial Library", library.buildingName)
            assertEquals("Library", library.resourceType)
            assertTrue(library.description.contains("Tanimura & Antle Family Memorial Library"))
            assertEquals(74, resources.size)
            assertEquals(74, resources.count { database.resourceHoursDao().getHours(it.resourceId) != null })
        } finally {
            database.close()
        }
    }
}
