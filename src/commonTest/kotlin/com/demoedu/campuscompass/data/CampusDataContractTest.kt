package com.demoedu.campuscompass.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Covers the optional catalog fields and the empty-result behavior used by the search screen.
 * These tests use an in-memory database so they exercise Room's column mapping rather than
 * relying only on the seed data's default values.
 */
class CampusDataContractTest {
    @Test
    fun resourceColumnsRoundTripIncludingOptionalValues() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            val resourceId = database.campusResourceDao().insertResource(
                CampusResourceEntity(
                    campusName = "Test Campus",
                    buildingName = "Resource Center",
                    buildingNumber = "TC-1",
                    resourceType = "Student Support",
                    resourceTag = "test-resource-center",
                    availability = false,
                    description = "A deliberately unavailable test resource.",
                    imageResource = "test_resource_center",
                    officialUrl = "https://example.edu/resource-center",
                ),
            )

            val saved = database.campusResourceDao().getResourcesAtCampus("Test Campus").single()
            assertEquals(resourceId, saved.resourceId)
            assertFalse(saved.availability)
            assertEquals("A deliberately unavailable test resource.", saved.description)
            assertEquals("test_resource_center", saved.imageResource)
            assertEquals("https://example.edu/resource-center", saved.officialUrl)
        } finally {
            database.close()
        }
    }

    @Test
    fun mapColumnsRoundTripAndMissingImageRemainsNull() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            ensureCampusMaps(database)

            val aptosMap = database.campusMapDao().getMapsAtCampus("Cabrillo Aptos Campus").single()
            assertEquals("Campus map", aptosMap.mapType)
            assertTrue(aptosMap.description.isNotBlank())
            assertEquals("campus_map_cabrillo_aptos", aptosMap.imageResource)
            assertTrue(aptosMap.officialUrl.startsWith("https://www.cabrillo.edu/"))

            // Watsonville has no published downloadable image. The null value is intentional:
            // the app must not substitute Aptos' map or invent an unrelated redirect.
            val watsonvilleMap = database.campusMapDao()
                .getMapsAtCampus("Cabrillo Watsonville Center")
                .single()
            assertNull(watsonvilleMap.imageResource)
            assertTrue(watsonvilleMap.officialUrl.contains("cabrillo.edu/maps-parking"))
        } finally {
            database.close()
        }
    }

    @Test
    fun seededCatalogPreservesRequiredIdentityAndMetadataContracts() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            ensureCampusResources(database)
            ensureMpcResources(database)
            ensureCabrilloResources(database)
            ensureGavilanResources(database)
            ensureHartnellResources(database)
            ensureUscResources(database)
            ensureCampusMaps(database)

            val resources = database.campusResourceDao().observeResources().first()
            assertTrue(resources.isNotEmpty())
            assertEquals(resources.size, resources.map { it.resourceTag }.toSet().size)
            resources.forEach { resource ->
                // These fields are the stable identity and user-facing minimum for every card.
                assertTrue(resource.campusName.isNotBlank())
                assertTrue(resource.buildingName.isNotBlank())
                assertTrue(resource.buildingNumber.isNotBlank())
                assertTrue(resource.resourceType.isNotBlank())
                assertTrue(resource.resourceTag.isNotBlank())
                assertTrue(resource.description.isNotBlank())
                assertTrue(resource.officialUrl == null || resource.officialUrl.startsWith("https://"))
                assertTrue(database.resourceHoursDao().getHours(resource.resourceId) != null)
            }

            val maps = database.campusMapDao().observeMaps().first()
            assertEquals(9, maps.size)
            assertEquals(
                maps.size,
                maps.map { "${it.campusName}:${it.mapType}" }.toSet().size,
            )
            maps.forEach { map ->
                assertTrue(map.campusName.isNotBlank())
                assertTrue(map.mapName.isNotBlank())
                assertTrue(map.mapType.isNotBlank())
                assertTrue(map.description.isNotBlank())
                assertTrue(map.officialUrl.startsWith("https://"))
            }
        } finally {
            database.close()
        }
    }

    @Test
    fun missingCampusAndResourceDataReturnEmptyOrNullInsteadOfLeakingOtherRows() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            ensureCampusResources(database)
            ensureCampusMaps(database)

            // An unknown campus is a valid empty state, not permission to return all campuses.
            assertTrue(database.campusResourceDao().getResourcesAtCampus("Unknown Campus").isEmpty())
            assertTrue(
                database.campusResourceDao().searchResources(
                    campusName = "Unknown Campus",
                    resourceType = null,
                    buildingQuery = "Library",
                ).isEmpty(),
            )
            assertTrue(database.campusMapDao().getMapsAtCampus("Unknown Campus").isEmpty())

            // A resource can exist before hours are published, so the UI receives no schedule
            // rather than a fabricated default schedule.
            val resourceWithoutHours = database.campusResourceDao().insertResource(
                CampusResourceEntity(
                    campusName = "Test Campus",
                    buildingName = "Unscheduled Resource",
                    buildingNumber = "TC-2",
                    resourceType = "Testing",
                    resourceTag = "test-unscheduled-resource",
                ),
            )
            assertNull(database.resourceHoursDao().getHours(resourceWithoutHours))

            // Flow-based catalog reads should still contain only the seeded campus data and the
            // explicitly inserted test row; no failed lookup may mutate the catalog.
            val resources = database.campusResourceDao().observeResources().first()
            assertTrue(resources.any { it.resourceId == resourceWithoutHours })
            assertTrue(resources.none { it.campusName == "Unknown Campus" })
        } finally {
            database.close()
        }
    }

    @Test
    fun verifiedAliasesSearchTheirOwnResourceWithoutChangingFormalName() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            ensureCampusResources(database)

            val studentCenter = database.campusResourceDao()
                .getResourcesAtCampus("CSUMB Main Campus")
                .single { it.buildingNumber == "12" }
            assertEquals("Student Center", studentCenter.buildingName)
            assertEquals(
                listOf("Redwood Hall"),
                database.campusResourceAliasDao().getAliases(studentCenter.resourceId).map { it.alias },
            )

            val aliasResults = database.campusResourceDao().searchResources(
                campusName = "CSUMB Main Campus",
                resourceType = null,
                buildingQuery = "Redwood Hall",
            )
            assertEquals(listOf(studentCenter.resourceId), aliasResults.map { it.resourceId })

            // Alias search must respect the same campus boundary as formal-name search.
            assertTrue(
                database.campusResourceDao().searchResources(
                    campusName = "MPC Monterey Campus",
                    resourceType = null,
                    buildingQuery = "Redwood Hall",
                ).isEmpty(),
            )
        } finally {
            database.close()
        }
    }
}
