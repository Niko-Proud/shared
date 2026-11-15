package com.demoedu.campuscompass.data

// These credentials are intentionally predictable for local demonstrations and automated checks.
const val testUsername = "testuser"
const val testPassword = "testpassword"

/** Adds one repeatable development account so the login screen can be tested immediately. */
suspend fun ensureTestUser(database: CampusDatabase) {
    if (database.userDao().findUser(testUsername) == null) {
        database.userDao().insertUser(
            UserEntity(username = testUsername, password = testPassword),
        )
    }
}

private data class SeedResource(
    val name: String,
    val number: String,
    val type: String,
    val description: String = "",
    val imageResource: String? = null,
    val officialUrl: String? = campusMapUrl,
    val hours: List<String> = defaultFacilityHours,
)

private const val campusMapUrl = "https://csumb.edu/media/csumb/documents/external---public-viewable/forms/administration/CampusMapUpdated_ACE.pdf"
private const val libraryUrl = "https://csumb.edu/library/"
private const val healthUrl = "https://csumb.edu/health/"
private const val studentUnionUrl = "https://csumb.edu/osu/services-resources/"
private const val mpcMapsUrl = "https://www.mpc.edu/campus-life/coming-to-campus/campus-maps.html"
private const val mpcStudentServicesUrl = "https://www.mpc.edu/about/student-services.html"
private const val mpcLibraryUrl = "https://www.mpc.edu/student-support/academic-support/library/"
private const val mpcTutoringUrl = "https://www.mpc.edu/student-support/academic-support/tutoring-centers/contact-and-hours.html"
private const val mpcHealthUrl = "https://www.mpc.edu/student-support/health-and-wellness/student-health-services/"
private const val mpcFacilitiesUrl = "https://www.mpc.edu/about/facilities-department.html"
private const val mpcCampusFacilitiesUrl = "https://www.mpc.edu/about/campus-facilities.html"
private const val mpcCampusLifeUrl = "https://www.mpc.edu/campus-life/index.html"
private const val mpcBasicNeedsUrl = "https://www.mpc.edu/student-support/health-and-wellness/basic-needs-center.html"
private const val mpcTrioUrl = "https://www.mpc.edu/student-support/find-your-community/trio/trio-learning-center.html"
private val defaultFacilityHours = List(7) { "See facility website" }
private val libraryHours = listOf(
    "8:00 AM–11:00 PM", "8:00 AM–11:00 PM", "8:00 AM–11:00 PM",
    "8:00 AM–11:00 PM", "8:00 AM–5:00 PM", "10:00 AM–5:00 PM", "2:00 PM–11:00 PM",
)
private val weekdayHours = List(5) { "8:00 AM–5:00 PM" } + listOf("Closed", "Closed")

/**
 * Seeds the searchable CSUMB building directory used by local development and demos.
 *
 * Building names, numbers, and map grid categories come from the official CSUMB campus
 * map (published January 2, 2025). Hours are only included where an official facility page
 * publishes a schedule; the remaining records intentionally point users back to the facility
 * website instead of inventing hours.
 * Sources: https://csumb.edu/media/csumb/documents/external---public-viewable/forms/administration/CampusMapUpdated_ACE.pdf,
 * https://csumb.edu/library/, and https://csumb.edu/health/medical-services/.
 */
suspend fun ensureCampusResources(database: CampusDatabase) {
    campusMapResources.forEach { seed ->
        val tag = "csumb-${seed.number.lowercase()}-${seed.name.slug()}"
        // The tag, not the auto-generated ID, is the stable identity of imported map data.
        // That lets a later seed refresh descriptions and links without duplicating rows.
        val existingId = database.campusResourceDao().getResourceIdByTag(tag)
        val resource = CampusResourceEntity(
            resourceId = existingId ?: 0,
            campusName = "CSUMB Main Campus",
            buildingName = seed.name,
            buildingNumber = seed.number,
            resourceType = seed.type,
            resourceTag = tag,
            description = seed.description.ifBlank { "${seed.type} facility at CSUMB: ${seed.name}." },
            imageResource = seed.imageResource,
            officialUrl = seed.officialUrl,
        )
        val resourceId = existingId ?: database.campusResourceDao().insertResource(resource)
        if (existingId != null) {
            database.campusResourceDao().updateResource(resource)
        }
        // Hours use resourceId as both their primary key and foreign key, so they are added
        // only after the resource exists and never create a second schedule row on relaunch.
        if (database.resourceHoursDao().getHoursResourceId(resourceId) == null) {
            database.resourceHoursDao().insertHours(
                ResourceHoursEntity(
                    resourceId = resourceId,
                    monday = seed.hours[0],
                    tuesday = seed.hours[1],
                    wednesday = seed.hours[2],
                    thursday = seed.hours[3],
                    friday = seed.hours[4],
                    saturday = seed.hours[5],
                    sunday = seed.hours[6],
                ),
            )
        }
    }
}

/**
 * Adds the verified MPC catalog while keeping its three locations distinct in the campus filter.
 * Building numbers and hours are only used where MPC publishes them; the official page remains
 * the source of truth for seasonal closures and details that are too fluid for this seed.
 * Sources: https://www.mpc.edu/campus-life/coming-to-campus/campus-maps.html,
 * https://www.mpc.edu/about/campus-facilities.html, and the linked MPC service pages below.
 */
suspend fun ensureMpcResources(database: CampusDatabase) {
    mpcResources.forEach { seed ->
        val tag = "mpc-${seed.campusKey}-${seed.number.lowercase()}-${seed.name.slug()}"
        val existingId = database.campusResourceDao().getResourceIdByTag(tag)
        val resource = CampusResourceEntity(
            resourceId = existingId ?: 0,
            campusName = seed.campusName,
            buildingName = seed.name,
            buildingNumber = seed.number,
            resourceType = seed.type,
            resourceTag = tag,
            description = seed.description.ifBlank { "${seed.type} resource at ${seed.campusName}: ${seed.name}." },
            imageResource = seed.imageResource,
            officialUrl = seed.officialUrl,
        )
        val resourceId = existingId ?: database.campusResourceDao().insertResource(resource)
        if (existingId != null) database.campusResourceDao().updateResource(resource)
        if (database.resourceHoursDao().getHoursResourceId(resourceId) == null) {
            database.resourceHoursDao().insertHours(
                ResourceHoursEntity(
                    resourceId = resourceId,
                    monday = seed.hours[0],
                    tuesday = seed.hours[1],
                    wednesday = seed.hours[2],
                    thursday = seed.hours[3],
                    friday = seed.hours[4],
                    saturday = seed.hours[5],
                    sunday = seed.hours[6],
                ),
            )
        }
    }
}

/**
 * Seeds map links separately so a future local map image can be added without changing resources.
 * MPC currently publishes three location maps from one maps page, while CSUMB publishes its
 * campus map as a standalone PDF. Keeping the source URL here makes both cases interchangeable
 * to the map UI without copying map files into the database.
 */
suspend fun ensureCampusMaps(database: CampusDatabase) {
    campusMaps.forEach { seed ->
        val existingId = database.campusMapDao().getMapId(seed.campusName, seed.mapType)
        val map = CampusMapEntity(
            mapId = existingId ?: 0,
            campusName = seed.campusName,
            mapName = seed.mapName,
            mapType = seed.mapType,
            description = seed.description,
            imageResource = seed.imageResource,
            officialUrl = seed.officialUrl,
        )
        if (existingId == null) database.campusMapDao().insertMap(map)
        else database.campusMapDao().updateMap(map)
    }
}

private fun String.slug(): String = lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')

private data class MpcSeedResource(
    val campusKey: String,
    val campusName: String,
    val name: String,
    val number: String,
    val type: String,
    val officialUrl: String,
    val description: String = "",
    val hours: List<String> = defaultFacilityHours,
    val imageResource: String? = null,
)

private data class CampusMapSeed(
    val campusName: String,
    val mapName: String,
    val mapType: String,
    val description: String,
    val officialUrl: String,
    val imageResource: String? = null,
)

private val mpcLibraryHours = listOf("8 AM–8 PM", "8 AM–8 PM", "8 AM–8 PM", "8 AM–8 PM", "8 AM–3 PM", "10 AM–2 PM", "See facility website")
private val mpcHealthHours = listOf("8 AM–5 PM", "8 AM–5 PM", "8 AM–5 PM", "8 AM–5 PM", "8 AM–12 PM", "Closed", "Closed")
private val mpcWeekdayHours = listOf("8 AM–5 PM", "8 AM–5 PM", "8 AM–5 PM", "8 AM–5 PM", "8 AM–12 PM", "Closed", "Closed")
private val mpcFitnessHours = listOf("8 AM–6 PM", "8 AM–6 PM", "8 AM–6 PM", "8 AM–6 PM", "8 AM–3 PM", "10 AM–1 PM", "Closed")
private val mpcStadiumHours = listOf("7 AM–5 PM", "7 AM–5 PM", "7 AM–5 PM", "7 AM–5 PM", "7 AM–5 PM", "7 AM–5 PM", "See facility website")

private val mpcResources = listOf(
    MpcSeedResource("monterey", "MPC Monterey Campus", "Welcome Center", "10", "Student Support", mpcCampusLifeUrl),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Administration Building", "11", "Administration / Services", mpcStudentServicesUrl),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Library and Technology Center", "16", "Library", mpcLibraryUrl, hours = mpcLibraryHours),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Student Services Building", "22", "Student Support", mpcStudentServicesUrl),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Student Center", "23", "Student Life", mpcCampusLifeUrl, hours = mpcWeekdayHours),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Kinesiology Building", "25", "Sports & Recreation", mpcCampusFacilitiesUrl),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Facilities Building", "27", "Facilities", mpcFacilitiesUrl, hours = mpcWeekdayHours),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Student Health Services", "22", "Health & Wellness", mpcHealthUrl, hours = mpcHealthHours),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Access Resource Center", "22", "Student Support", "https://www.mpc.edu/student-support/find-your-community/arc/contact.html"),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Tutoring & Academic Success Center", "16", "Academic Support", mpcTutoringUrl, hours = listOf("9 AM–6 PM", "9 AM–6 PM", "9 AM–6 PM", "9 AM–6 PM", "9 AM–2 PM", "Closed", "Closed")),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Reading & Writing Center", "16", "Academic Support", mpcTutoringUrl, hours = listOf("8 AM–6 PM", "8 AM–6 PM", "8 AM–6 PM", "8 AM–6 PM", "8 AM–2 PM", "10 AM–2 PM", "Closed")),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Math Learning Center", "16", "Academic Support", mpcTutoringUrl),
    MpcSeedResource("monterey", "MPC Monterey Campus", "STEM Tutoring & Study Center", "16", "Academic Support", mpcTutoringUrl),
    MpcSeedResource("monterey", "MPC Monterey Campus", "TRIO Learning Center", "23", "Academic Support", mpcTrioUrl),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Basic Needs Center", "23", "Student Support", mpcBasicNeedsUrl),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Fitness & Weight Training Center", "25", "Sports & Recreation", mpcCampusFacilitiesUrl, hours = mpcFitnessHours),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Pappas-Phillips Community Stadium", "25", "Sports & Recreation", mpcCampusFacilitiesUrl, hours = mpcStadiumHours),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Swimming Pool", "25", "Sports & Recreation", mpcCampusFacilitiesUrl),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Softball Field", "25", "Sports & Recreation", mpcCampusFacilitiesUrl),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Baseball Field", "25", "Sports & Recreation", mpcCampusFacilitiesUrl),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Gymnasium", "25", "Sports & Recreation", mpcCampusFacilitiesUrl),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Tennis & Pickleball Courts", "25", "Sports & Recreation", mpcCampusFacilitiesUrl),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Dance Building", "25", "Sports & Recreation", mpcCampusFacilitiesUrl),
    MpcSeedResource("monterey", "MPC Monterey Campus", "Adapted Physical Education Building", "25", "Sports & Recreation", mpcCampusFacilitiesUrl),
    MpcSeedResource("marina", "MPC Marina Campus", "Lobo Hub", "MA-201", "Student Support", "https://www.mpc.edu/about/campuses-and-centers/marina-campus.html"),
    MpcSeedResource("seaside", "MPC Public Safety Training Center", "Public Safety Training Center", "PSTC", "Public Safety", mpcMapsUrl),
)

private val campusMaps = listOf(
    CampusMapSeed("CSUMB Main Campus", "CSUMB Campus Map", "Campus map", "Building directory, parking, transit, and campus landmarks.", campusMapUrl),
    CampusMapSeed("MPC Monterey Campus", "MPC Monterey Campus Map", "Campus map", "Monterey campus map with building numbers and directions.", mpcMapsUrl),
    CampusMapSeed("MPC Marina Campus", "MPC Marina Campus Map", "Campus map", "Marina Education Center map with building numbers and directions.", mpcMapsUrl),
    CampusMapSeed("MPC Public Safety Training Center", "MPC Seaside Public Safety Training Center Map", "Campus map", "Public Safety Training Center map with building numbers and directions.", mpcMapsUrl),
)

private val campusMapResources = listOf(
    SeedResource("Administration Building", "1", "Administration / Services"),
    SeedResource("Alumni & Visitors Center", "97", "Administration / Services"),
    SeedResource("Beach Hall", "21", "Academic Building"),
    SeedResource("Black Box Cabaret", "81", "Arts & Events"),
    SeedResource("Business Information and Technology", "506", "Academic Building"),
    SeedResource("Central Plant", "74", "Facilities"),
    SeedResource("Chapman Science Academic Center", "53", "Academic Building"),
    SeedResource("College of Arts, Humanities, and Social Sciences", "504", "Academic Building"),
    SeedResource("Cinematic Arts & Technology", "27", "Academic Building"),
    SeedResource("Coast Hall", "45", "Academic Building"),
    SeedResource("Del Mar", "3", "Academic Building"),
    SeedResource("Dining Commons", "16", "Dining"),
    SeedResource("Dunes Hall", "10", "Academic Building"),
    SeedResource("Facilities Management", "37", "Facilities"),
    SeedResource("Forest Hall", "49", "Academic Building"),
    SeedResource("Gavilan Hall", "201", "Academic Building"),
    SeedResource("Green Hall", "58", "Academic Building"),
    SeedResource("Grove Hall", "48", "Academic Building"),
    SeedResource("Harbor Hall", "46", "Academic Building"),
    SeedResource("Health & Wellness Services", "80", "Health & Wellness", officialUrl = healthUrl, hours = weekdayHours),
    SeedResource("Heron Hall", "18", "Academic Building"),
    SeedResource("IT Services", "43", "Technology Support"),
    SeedResource("Mail Room / Shipping & Receiving", "35", "Administration / Services"),
    SeedResource("Meeting House", "98", "Student Life"),
    SeedResource("Mountain Hall", "84", "Academic Building"),
    SeedResource("Music Hall", "30", "Arts & Events"),
    SeedResource("Oaks Hall", "490", "Academic Building"),
    SeedResource("Oaks Hall Annex", "491", "Academic Building"),
    SeedResource("Oaks Hall Storage", "690", "Facilities"),
    SeedResource("Ocean Hall", "86", "Academic Building"),
    SeedResource("Otter Express", "14", "Dining"),
    SeedResource("Otter Student Union", "552", "Student Life", officialUrl = studentUnionUrl),
    SeedResource("Pacific Hall", "44", "Academic Building"),
    SeedResource("Playa", "2", "Academic Building"),
    SeedResource("Reading Center", "59", "Student Support"),
    SeedResource("Sand Hall", "8", "Academic Building"),
    SeedResource("Science Instructional Lab Annex", "50", "Academic Building"),
    SeedResource("Science Research Lab Annex", "13", "Academic Building"),
    SeedResource("Student Center", "12", "Student Life"),
    SeedResource("Student Services", "47", "Student Support"),
    SeedResource("Surf Hall", "6", "Academic Building"),
    SeedResource("Tanimura & Antle Family Memorial Library", "508", "Library", officialUrl = libraryUrl, hours = libraryHours),
    SeedResource("Telecommunications", "41", "Technology Support"),
    SeedResource("Tide Hall", "23", "Academic Building"),
    SeedResource("University Center", "29", "Student Life"),
    SeedResource("University Storage", "36", "Facilities"),
    SeedResource("Valley Hall", "82", "Academic Building"),
    SeedResource("Visual & Public Art – A", "70", "Arts & Events"),
    SeedResource("Visual & Public Art – B", "71", "Arts & Events"),
    SeedResource("Visual & Public Art – C", "72", "Arts & Events"),
    SeedResource("Visual & Public Art – D", "73", "Arts & Events"),
    SeedResource("Watershed Institute", "42", "Academic Building"),
    SeedResource("Wave Hall", "4", "Academic Building"),
    SeedResource("World Theater", "28", "Arts & Events"),
    SeedResource("Asilomar Hall", "203", "Residence Hall"),
    SeedResource("Avocet Hall", "208", "Residence Hall"),
    SeedResource("Cypress Hall", "202", "Residence Hall"),
    SeedResource("Manzanita Hall", "205", "Residence Hall"),
    SeedResource("Promontory – West", "325", "Residence Hall"),
    SeedResource("Promontory – Center", "326", "Residence Hall"),
    SeedResource("Promontory – East", "327", "Residence Hall"),
    SeedResource("Pinnacles Suites", "302", "Residence Hall"),
    SeedResource("Sanderling Hall", "211", "Residence Hall"),
    SeedResource("Strawberry Apartments", "301", "Residence Hall"),
    SeedResource("Tortuga Hall", "210", "Residence Hall"),
    SeedResource("Vineyard Suites", "303", "Residence Hall"),
    SeedResource("Willet Hall", "204", "Residence Hall"),
    SeedResource("Yarrow Hall", "206", "Residence Hall"),
    SeedResource("Aquatic Center", "100", "Sports & Recreation"),
    SeedResource("Field House", "902", "Sports & Recreation"),
    SeedResource("Field Office", "902C", "Sports & Recreation"),
    SeedResource("Otter Sports Center", "90", "Sports & Recreation"),
    SeedResource("Stadium Restrooms", "95", "Sports & Recreation"),
)
