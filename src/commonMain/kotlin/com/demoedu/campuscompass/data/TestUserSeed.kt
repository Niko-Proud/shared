package com.demoedu.campuscompass.data

// These credentials are intentionally predictable for local demonstrations and automated checks.
const val testUsername = "testuser"
const val testPassword = "testpassword"

/**
 * Adds one repeatable development account so the login screen can be tested immediately.
 * Existing testuser rows are refreshed deliberately: local databases survive app rebuilds,
 * so leaving an old password in place would make the credentials printed in this file stale.
 */
suspend fun ensureTestUser(database: CampusDatabase) {
    val existingUser = database.userDao().findUser(testUsername)
    if (existingUser == null) {
        database.userDao().insertUser(UserEntity(username = testUsername, password = testPassword))
    } else if (existingUser.password != testPassword) {
        // Only the predictable development account is repaired; user-created accounts remain untouched.
        database.userDao().updatePassword(existingUser.usrId, testPassword)
    }
}

private interface CampusSeedRecord {
    val campusKey: String
    val campusName: String
    val name: String
    val number: String
    val type: String
    val description: String
    val imageResource: String?
    val officialUrl: String?
    val aliases: List<String>
    val hours: List<String>
}

private data class SeedResource(
    override val name: String,
    override val number: String,
    override val type: String,
    override val description: String = "",
    override val imageResource: String? = null,
    override val officialUrl: String? = campusMapUrl,
    override val hours: List<String> = defaultFacilityHours,
    override val campusKey: String = "",
    override val campusName: String = "CSUMB Main Campus",
    override val aliases: List<String> = emptyList(),
) : CampusSeedRecord

private const val campusMapUrl = "https://csumb.edu/media/csumb/documents/external---public-viewable/forms/administration/CampusMapUpdated_ACE.pdf"
private const val libraryUrl = "https://csumb.edu/library/"
private const val healthUrl = "https://csumb.edu/health/"
private const val studentUnionUrl = "https://csumb.edu/osu/services-resources/"
private const val mpcMapsUrl = "https://www.mpc.edu/campus-life/coming-to-campus/campus-maps.html"
private const val mpcStudentServicesUrl = "https://www.mpc.edu/about/student-services.html"
private const val mpcLibraryUrl = "https://mpc.edu/student-support/academic-support/library/index.html"
private const val mpcTutoringUrl = "https://www.mpc.edu/student-support/academic-support/tutoring-centers/contact-and-hours.html"
private const val mpcHealthUrl = "https://mpc.edu/student-support/health-and-wellness/student-health-services/"
private const val mpcFacilitiesUrl = "https://www.mpc.edu/about/facilities-department.html"
private const val mpcCampusFacilitiesUrl = "https://www.mpc.edu/about/campus-facilities.html"
private const val mpcCampusLifeUrl = "https://www.mpc.edu/campus-life/index.html"
private const val mpcBasicNeedsUrl = "https://www.mpc.edu/student-support/health-and-wellness/basic-needs-center.html"
private const val mpcTrioUrl = "https://www.mpc.edu/student-support/find-your-community/trio/trio-learning-center.html"
private const val cabrilloMapsUrl = "https://www.cabrillo.edu/maps-parking/"
private const val cabrilloLibraryUrl = "https://www.cabrillo.edu/library/"
private const val cabrilloLibraryHoursUrl = "https://www.cabrillo.edu/es/library/es-hours/"
private const val cabrilloTutoringUrl = "https://www.cabrillo.edu/tutoring-and-academic-support/"
private const val cabrilloHealthUrl = "https://www.cabrillo.edu/student-health-services/"
private const val cabrilloLearningCentersUrl = "https://www.cabrillo.edu/learning-centers/"
private const val cabrilloWelcomeUrl = "https://www.cabrillo.edu/welcome-center/"
private const val gavilanMapUrl = "https://www.gavilan.edu/finaid/documents/Campus_Map.pdf"
private const val gavilanLearningCommonsUrl = "https://www.gavilan.edu/student/learningcommons/index.php"
private const val gavilanLsrcNewsUrl = "https://www.gavilan.edu/news/student_services_offices_move_into_new_library_and_student_resource_center.php"
private const val hartnellMapsUrl = "https://www.hartnell.edu/location/detailed-maps.html"
private const val hartnellLibraryUrl = "https://www.hartnell.edu/library/"
private const val hartnellTutoringUrl = "https://www.hartnell.edu/support/tutoring/index.html"
private const val hartnellFacilitiesUrl = "https://www.hartnell.edu/about/facilities/index.html"
private const val hartnellGuideUrl = "https://www.hartnell.edu/about/studentguide/index.html"
private const val ucscMapsUrl = "https://www.ucsc.edu/campus/visit/maps-directions/"
private const val ucscPrintableMapUrl = "https://maps.ucsc.edu/printable-maps/Campus-Map-Poster.pdf"
private const val ucscLibraryUrl = "https://library.ucsc.edu/"
private const val ucscHealthUrl = "https://health.ucsc.edu/about/contact/"
private const val ucscTransitUrl = "https://transportation.ucsc.edu/buses-shuttles/campus-shuttles/"
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
    ensureCampusDirectoryResources(database, "csumb", campusMapResources)
}

/**
 * Adds the verified MPC catalog while keeping its three locations distinct in the campus filter.
 * Building numbers and hours are only used where MPC publishes them; the official page remains
 * the source of truth for seasonal closures and details that are too fluid for this seed.
 * Sources: https://www.mpc.edu/campus-life/coming-to-campus/campus-maps.html,
 * https://www.mpc.edu/about/campus-facilities.html, and the linked MPC service pages below.
 */
suspend fun ensureMpcResources(database: CampusDatabase) {
    ensureCampusDirectoryResources(database, "mpc", mpcResources)
}

/**
 * Adds Cabrillo's Aptos and Watsonville locations from the official map and service pages.
 * The map uses labels such as "SAC East", "HW1", and "VAPA 1000" rather than one numeric
 * building sequence, so those labels are retained as buildingNumber instead of being guessed.
 * Sources: https://www.cabrillo.edu/maps-parking/, https://www.cabrillo.edu/library/,
 * https://www.cabrillo.edu/student-health-services/, and https://www.cabrillo.edu/tutoring-and-academic-support/.
 */
suspend fun ensureCabrilloResources(database: CampusDatabase) {
    ensureCampusDirectoryResources(database, "cabrillo", cabrilloResources)
}

/**
 * Adds the verified Gavilan, Hartnell, and UC Santa Cruz catalogs used by local testing.
 * A map label is still useful when no dedicated service page exists, so these records keep
 * nullable officialUrl values instead of redirecting students to a guessed department page.
 */
/** Writes only Gavilan's Gilroy catalog, keeping its map labels and source links together. */
suspend fun ensureGavilanResources(database: CampusDatabase) {
    ensureCampusDirectoryResources(database, "gavilan", gavilanResources)
}

/** Writes Hartnell's main campus and education-center records as one campus-specific group. */
suspend fun ensureHartnellResources(database: CampusDatabase) {
    ensureCampusDirectoryResources(database, "hartnell", hartnellResources)
}

/** Writes UCSC's residential-campus resources without mixing them into the community-college data. */
suspend fun ensureUscResources(database: CampusDatabase) {
    ensureCampusDirectoryResources(database, "ucsc", ucscResources)
}

/**
 * Shared idempotent writer for every institution catalog. The stable source key and map label
 * form the resource tag, so relaunches update metadata without duplicating rows. The blank
 * CSUMB campus key preserves the original CSUMB tags already used by existing local databases.
 */
private suspend fun ensureCampusDirectoryResources(
    database: CampusDatabase,
    sourceKey: String,
    resources: List<CampusSeedRecord>,
) {
    resources.forEach { seed ->
        // Include the source and campus key in the tag because building numbers are often reused
        // across campuses. The tag is stable across launches and makes reseeding an update.
        val campusPart = seed.campusKey.takeUnless { it.isBlank() }?.let { "$it-" } ?: ""
        val tag = "$sourceKey-$campusPart${seed.number.lowercase()}-${seed.name.slug()}"
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
        // Seed aliases are authoritative for this local catalog. Replacing only this resource's
        // aliases keeps reruns deterministic while leaving all other campus rows untouched.
        database.campusResourceAliasDao().deleteAliasesForResource(resourceId)
        seed.aliases.distinct().filter { it.isNotBlank() }.forEach { alias ->
            database.campusResourceAliasDao().insertAlias(
                CampusResourceAliasEntity(resourceId = resourceId, alias = alias),
            )
        }
        // Hours are inserted only once so a later seed run does not erase locally maintained
        // schedule changes. The resource metadata above is still refreshed on every run.
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
        // The unique campus/type index gives each map a stable logical key while allowing its
        // image resource or official URL to be corrected safely on the next app launch.
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
    override val campusKey: String,
    override val campusName: String,
    override val name: String,
    override val number: String,
    override val type: String,
    override val officialUrl: String,
    override val description: String = "",
    override val hours: List<String> = defaultFacilityHours,
    override val imageResource: String? = null,
    override val aliases: List<String> = emptyList(),
) : CampusSeedRecord

private data class CabrilloSeedResource(
    override val campusKey: String,
    override val campusName: String,
    override val name: String,
    override val number: String,
    override val type: String,
    override val officialUrl: String?,
    override val description: String = "",
    override val hours: List<String> = defaultFacilityHours,
    override val imageResource: String? = null,
    override val aliases: List<String> = emptyList(),
) : CampusSeedRecord

private data class CampusDirectorySeedResource(
    override val campusKey: String,
    override val campusName: String,
    override val name: String,
    override val number: String,
    override val type: String,
    override val officialUrl: String? = null,
    override val description: String = "",
    override val hours: List<String> = defaultFacilityHours,
    override val imageResource: String? = null,
    override val aliases: List<String> = emptyList(),
) : CampusSeedRecord

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
private val cabrilloLibraryHours = listOf("9 AM–8 PM", "9 AM–8 PM", "9 AM–8 PM", "9 AM–8 PM", "9 AM–4 PM", "Closed", "Closed")
private val cabrilloHealthHours = listOf("8 AM–5 PM", "8 AM–5 PM", "8 AM–5 PM", "8 AM–5 PM", "8 AM–12 PM", "Closed", "Closed")
private val cabrilloHubHours = listOf("9 AM–6 PM", "9 AM–6 PM", "9 AM–6 PM", "9 AM–6 PM", "10 AM–3 PM", "Closed", "11 AM–5 PM")
private val gavilanWeekdayHours = listOf("8 AM–5 PM", "8 AM–5 PM", "8 AM–5 PM", "8 AM–5 PM", "8 AM–4 PM", "Closed", "Closed")
private val hartnellLibraryHours = listOf("8 AM–9 PM", "8 AM–9 PM", "8 AM–9 PM", "8 AM–9 PM", "8 AM–4 PM", "10 AM–2 PM", "Closed")
private val hartnellTutoringHours = listOf("10 AM–6 PM", "10 AM–6 PM", "10 AM–6 PM", "10 AM–6 PM", "10 AM–3 PM", "Closed", "Closed")
private val ucscWeekdayHours = listOf("8 AM–5 PM", "8 AM–5 PM", "8 AM–5 PM", "8 AM–5 PM", "8 AM–5 PM", "Closed", "Closed")

private val mpcResources = listOf(
    MpcSeedResource("monterey", "MPC Monterey Campus", "Welcome Center", "10", "Student Support", mpcCampusLifeUrl, aliases = listOf("Building 10")),
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
    MpcSeedResource("marina", "MPC Marina Campus", "Lobo Hub", "MA-201", "Student Support", "https://www.mpc.edu/about/campuses-and-centers/marina-campus/index.html"),
    MpcSeedResource("seaside", "MPC Public Safety Training Center", "Public Safety Training Center", "PSTC", "Public Safety", mpcMapsUrl),
)

private val cabrilloResources = listOf(
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "Welcome Center", "SAC East", "Student Support", cabrilloWelcomeUrl),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "SAC West", "SAC West", "Administration / Services", null),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "Career / Transfer Center", "100", "Student Support", "https://www.cabrillo.edu/counseling/"),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "Business, English, Language Arts", "300", "Academic Building", null),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "Computer Information Systems", "800", "Academic Building", null),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "Cafeteria", "900", "Dining", null),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "Swenson Library", "1000", "Library", cabrilloLibraryHoursUrl, hours = cabrilloLibraryHours, aliases = listOf("Library", "Learning Resources Center", "LRC", "Library and TLC")),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "Tutoring HUB", "1000", "Academic Support", cabrilloTutoringUrl, hours = cabrilloHubHours),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "STEM Center", "834", "Academic Support", cabrilloLearningCentersUrl),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "Student Health Services", "912", "Health & Wellness", cabrilloHealthUrl, hours = cabrilloHealthHours),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "Health & Wellness Center", "HW1", "Health & Wellness", cabrilloHealthUrl),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "Medical Assistant and Nursing", "HW2", "Academic Building", null),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "Gym", "1100", "Sports & Recreation", null),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "Facilities / Receiving", "2000", "Facilities", null),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "Administrative Offices / Purchasing", "2030", "Administration / Services", null),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "Cabrillo Extension Office", "2100A", "Administration / Services", null),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "Small Business Development Center", "2100C", "Student Support", null),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "VAPA 1000", "VAPA 1000", "Arts & Events", null),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "VAPA 2000", "VAPA 2000", "Arts & Events", null),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "VAPA 3000", "VAPA 3000", "Arts & Events", null),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "VAPA 5000", "VAPA 5000", "Arts & Events", null),
    CabrilloSeedResource("aptos", "Cabrillo Aptos Campus", "Sesnon House", "1800", "Student Life", null),
    CabrilloSeedResource("watsonville", "Cabrillo Watsonville Center", "Watsonville Center", "A", "Student Support", null),
    CabrilloSeedResource("watsonville", "Cabrillo Watsonville Center", "Integrated Learning Center", "A210/A260", "Academic Support", "https://www.cabrillo.edu/watsonville-center/integrated-learning-center/"),
    CabrilloSeedResource("watsonville", "Cabrillo Watsonville Center", "Student Health Services Clinic", "A120", "Health & Wellness", cabrilloHealthUrl),
)

// Sources: Gavilan's campus map, Learning Commons page, and LSRC announcement.
private val gavilanResources = listOf(
    CampusDirectorySeedResource("gilroy", "Gavilan Gilroy Campus", "Library and Student Resource Center", "LSRC", "Library / Student Support", gavilanLsrcNewsUrl, "New shared library and student-services building; the official announcement describes its phased opening.", gavilanWeekdayHours, aliases = listOf("LSRC")),
    CampusDirectorySeedResource("gilroy", "Gavilan Gilroy Campus", "Student Center / Administration", "SC", "Student Support"),
    CampusDirectorySeedResource("gilroy", "Gavilan Gilroy Campus", "Tutoring Center", "LI", "Academic Support", gavilanLearningCommonsUrl),
    CampusDirectorySeedResource("gilroy", "Gavilan Gilroy Campus", "Career / Transfer Center", "SC", "Student Support"),
    CampusDirectorySeedResource("gilroy", "Gavilan Gilroy Campus", "Library", "LI", "Library", gavilanLearningCommonsUrl),
    CampusDirectorySeedResource("gilroy", "Gavilan Gilroy Campus", "Health Occupations Building", "HOB", "Academic Building"),
    CampusDirectorySeedResource("gilroy", "Gavilan Gilroy Campus", "Gym / Adaptive Physical Education", "GY / APE", "Sports & Recreation"),
    CampusDirectorySeedResource("gilroy", "Gavilan Gilroy Campus", "Business Building", "BU", "Academic Building"),
    CampusDirectorySeedResource("gilroy", "Gavilan Gilroy Campus", "Humanities Building", "HU", "Academic Building"),
    CampusDirectorySeedResource("gilroy", "Gavilan Gilroy Campus", "Life Science Building", "LS", "Academic Building"),
    CampusDirectorySeedResource("gilroy", "Gavilan Gilroy Campus", "Physical Science Building", "PH", "Academic Building"),
    CampusDirectorySeedResource("gilroy", "Gavilan Gilroy Campus", "Theater", "TH", "Arts & Events"),
    CampusDirectorySeedResource("gilroy", "Gavilan Gilroy Campus", "Child Development Center", "CD", "Student Support"),
    CampusDirectorySeedResource("gilroy", "Gavilan Gilroy Campus", "Police Academy", "PA", "Public Safety"),
)

// Sources: Hartnell's detailed building maps, library, facilities, tutoring, and student guide pages.
private val hartnellResources = listOf(
    CampusDirectorySeedResource("main", "Hartnell Main Campus", "Library / Learning Resource Center", "A", "Library", hartnellLibraryUrl, hours = hartnellLibraryHours, aliases = listOf("Library", "Learning Resource Center")),
    CampusDirectorySeedResource("main", "Hartnell Main Campus", "Student Services", "B", "Student Support", hartnellGuideUrl),
    CampusDirectorySeedResource("main", "Hartnell Main Campus", "Student Center", "C", "Student Life"),
    CampusDirectorySeedResource("main", "Hartnell Main Campus", "College Administration North", "D", "Administration / Services"),
    CampusDirectorySeedResource("main", "Hartnell Main Campus", "College Administration South", "E", "Administration / Services"),
    CampusDirectorySeedResource("main", "Hartnell Main Campus", "Weightroom", "F", "Sports & Recreation"),
    CampusDirectorySeedResource("main", "Hartnell Main Campus", "Auxiliary Gym", "G", "Sports & Recreation"),
    CampusDirectorySeedResource("main", "Hartnell Main Campus", "Main Gym", "H", "Sports & Recreation"),
    CampusDirectorySeedResource("main", "Hartnell Main Campus", "Visual Arts", "J", "Arts & Events"),
    CampusDirectorySeedResource("main", "Hartnell Main Campus", "Performing Arts / Music", "K", "Arts & Events"),
    CampusDirectorySeedResource("main", "Hartnell Main Campus", "Child Development Center", "M", "Student Support"),
    CampusDirectorySeedResource("main", "Hartnell Main Campus", "Merrill Hall", "N", "Academic Building"),
    CampusDirectorySeedResource("main", "Hartnell Main Campus", "Nursing and Health Sciences", "O", "Academic Building"),
    CampusDirectorySeedResource("main", "Hartnell Main Campus", "Welcome Center", "R", "Student Support"),
    CampusDirectorySeedResource("main", "Hartnell Main Campus", "Planetarium", "S", "Arts & Events"),
    CampusDirectorySeedResource("main", "Hartnell Main Campus", "STEM Center", "S", "Academic Support", aliases = listOf("STEM Building")),
    CampusDirectorySeedResource("main", "Hartnell Main Campus", "Campus Safety", "T", "Public Safety"),
    CampusDirectorySeedResource("main", "Hartnell Main Campus", "Panther Learning Lab", "B230", "Academic Support", hartnellTutoringUrl, hours = hartnellTutoringHours, aliases = listOf("PLL", "Tutoring")),
    CampusDirectorySeedResource("main", "Hartnell Main Campus", "Facilities Services", "Facilities", "Facilities", hartnellFacilitiesUrl),
    CampusDirectorySeedResource("alisal", "Hartnell Alisal Campus", "Alisal Campus", "Alisal", "Education Center"),
    CampusDirectorySeedResource("king-city", "Hartnell King City Education Center", "King City Education Center", "KCEC", "Education Center", "https://www.hartnell.edu/kcec/"),
    CampusDirectorySeedResource("castroville", "Hartnell Castroville Education Center", "Castroville Education Center", "Castroville", "Education Center"),
    CampusDirectorySeedResource("soledad", "Hartnell Soledad Education Center", "Soledad Education Center", "Soledad", "Education Center"),
)

// Sources: UCSC's campus maps, University Library, Cowell Health Center, and campus transit pages.
private val ucscResources = listOf(
    CampusDirectorySeedResource("main", "UC Santa Cruz Main Campus", "McHenry Library", "McHenry", "Library", ucscLibraryUrl, aliases = listOf("McHenry")),
    CampusDirectorySeedResource("main", "UC Santa Cruz Main Campus", "Cowell Student Health Center", "Cowell", "Health & Wellness", ucscHealthUrl),
    CampusDirectorySeedResource("main", "UC Santa Cruz Main Campus", "Campus Transit", "TAPS", "Transportation", ucscTransitUrl),
    CampusDirectorySeedResource("main", "UC Santa Cruz Main Campus", "Cowell College", "Cowell", "Residential College"),
    CampusDirectorySeedResource("main", "UC Santa Cruz Main Campus", "Stevenson College", "Stevenson", "Residential College"),
    CampusDirectorySeedResource("main", "UC Santa Cruz Main Campus", "Crown College", "Crown", "Residential College"),
    CampusDirectorySeedResource("main", "UC Santa Cruz Main Campus", "Merrill College", "Merrill", "Residential College"),
    CampusDirectorySeedResource("main", "UC Santa Cruz Main Campus", "Porter College", "Porter", "Residential College"),
    CampusDirectorySeedResource("main", "UC Santa Cruz Main Campus", "Kresge College", "Kresge", "Residential College"),
    CampusDirectorySeedResource("main", "UC Santa Cruz Main Campus", "Oakes College", "Oakes", "Residential College"),
    CampusDirectorySeedResource("main", "UC Santa Cruz Main Campus", "Rachel Carson College", "Rachel Carson", "Residential College"),
    CampusDirectorySeedResource("main", "UC Santa Cruz Main Campus", "College Nine", "College Nine", "Residential College"),
    CampusDirectorySeedResource("main", "UC Santa Cruz Main Campus", "College Ten", "College Ten", "Residential College"),
    CampusDirectorySeedResource("main", "UC Santa Cruz Main Campus", "Physical Sciences Building", "PSB", "Academic Building"),
    CampusDirectorySeedResource("main", "UC Santa Cruz Main Campus", "Science and Engineering Library", "SEL", "Library", ucscLibraryUrl, aliases = listOf("S&E Library", "Science & Engineering Library")),
)

/**
 * Maps keep both the local visual asset and the official source URL. The PNGs are cropped from
 * the published map files so the app can display the campus itself without forcing the user
 * through a building directory or navigation panel; the URL remains available for provenance.
 * Watsonville intentionally has no image until Cabrillo publishes a downloadable map asset.
 */
private val campusMaps = listOf(
    CampusMapSeed("CSUMB Main Campus", "CSUMB Campus Map", "Campus map", "Building directory, parking, transit, and campus landmarks.", campusMapUrl, imageResource = "campus_map_csumb"),
    CampusMapSeed("MPC Monterey Campus", "MPC Monterey Campus Map", "Campus map", "Monterey campus map with building numbers and directions.", mpcMapsUrl, imageResource = "campus_map_mpc_monterey"),
    CampusMapSeed("MPC Marina Campus", "MPC Marina Campus Map", "Campus map", "Marina Education Center map with building numbers and directions.", mpcMapsUrl, imageResource = "campus_map_mpc_marina"),
    CampusMapSeed("MPC Public Safety Training Center", "MPC Seaside Public Safety Training Center Map", "Campus map", "Public Safety Training Center map with building numbers and directions.", mpcMapsUrl, imageResource = "campus_map_mpc_pstc"),
    CampusMapSeed("Cabrillo Aptos Campus", "Cabrillo Aptos Campus Map", "Campus map", "Aptos campus map, building directory, parking, accessibility paths, and campus services.", cabrilloMapsUrl, imageResource = "campus_map_cabrillo_aptos"),
    CampusMapSeed("Cabrillo Watsonville Center", "Cabrillo Watsonville Center Map", "Campus map", "Watsonville Center map and directions.", cabrilloMapsUrl),
    CampusMapSeed("Gavilan Gilroy Campus", "Gavilan College Gilroy Campus Map", "Campus map", "Gilroy campus building guide, services, parking, and athletic facilities.", gavilanMapUrl, imageResource = "campus_map_gavilan_gilroy"),
    CampusMapSeed("Hartnell Main Campus", "Hartnell College Main Campus Map", "Campus map", "Main campus buildings, centers, athletics, parking, and campus locations.", hartnellMapsUrl, imageResource = "campus_map_hartnell_main"),
    CampusMapSeed("UC Santa Cruz Main Campus", "UC Santa Cruz Campus Map", "Campus map", "Residential campus map with buildings, paths, shuttle stops, parking, and accessibility information.", ucscPrintableMapUrl, imageResource = "campus_map_ucsc"),
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
    SeedResource("Otter Student Union", "552", "Student Life", officialUrl = studentUnionUrl, aliases = listOf("OSU")),
    SeedResource("Pacific Hall", "44", "Academic Building"),
    SeedResource("Playa", "2", "Academic Building"),
    SeedResource("Reading Center", "59", "Student Support"),
    SeedResource("Sand Hall", "8", "Academic Building"),
    SeedResource("Science Instructional Lab Annex", "50", "Academic Building"),
    SeedResource("Science Research Lab Annex", "13", "Academic Building"),
    // CSUMB's current public event materials call Building 12 Redwood Hall; retain Student
    // Center as the formal map name and make the public alias searchable.
    SeedResource("Student Center", "12", "Student Life", aliases = listOf("Redwood Hall")),
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
