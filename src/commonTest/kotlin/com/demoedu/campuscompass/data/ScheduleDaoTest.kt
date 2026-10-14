package com.demoedu.campuscompass.data

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Checks user-owned schedules and their resource/hours join used by the app. */
class ScheduleDaoTest {
    @Test
    fun schedulesBelongToOneUserAndReturnResourceDetails() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            val userId = database.userDao().insertUser(UserEntity(username = "planner", password = "secret"))
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
            val scheduleId = database.scheduleDao().insertSchedule(
                ScheduleEntity(
                    usrId = userId,
                    resourceId = resourceId,
                    scheduleName = "Library study block",
                    dayOfWeek = 1,
                    startTime = "09:00",
                    endTime = "11:00",
                    notes = "Bring project notes",
                ),
            )

            val schedule = database.scheduleDao().getScheduleForUser(userId).single()
            assertEquals(scheduleId, schedule.scheduleId)
            assertEquals(resourceId, database.scheduleDao().getScheduleResourceId(scheduleId))
            assertEquals("planner", database.userDao().getUsername(schedule.usrId))
            assertEquals("Library study block", database.scheduleDao().getScheduleName(scheduleId))

            val joined = database.scheduleDao().getScheduleWithResources(userId).single()
            assertEquals("Main Library", joined.buildingName)
            assertEquals("LIB-100", joined.buildingNumber)
            assertEquals("08:00-20:00", joined.monday)

            assertEquals(1, database.scheduleDao().deleteSchedule(scheduleId, userId))
            assertTrue(database.scheduleDao().getScheduleForUser(userId).isEmpty())
        } finally {
            database.close()
        }
    }

    @Test
    fun deletingAUsersScheduleDoesNotDeleteAnotherUsersSchedule() = runTest {
        val database = createInMemoryCampusDatabase()
        try {
            val firstUser = database.userDao().insertUser(UserEntity(username = "first", password = "one"))
            val secondUser = database.userDao().insertUser(UserEntity(username = "second", password = "two"))
            val resourceId = database.campusResourceDao().insertResource(
                CampusResourceEntity(
                    campusName = "North",
                    buildingName = "Library",
                    buildingNumber = "1",
                    resourceType = "Study",
                    resourceTag = "library",
                ),
            )
            val firstSchedule = database.scheduleDao().insertSchedule(
                ScheduleEntity(usrId = firstUser, resourceId = resourceId, scheduleName = "First", dayOfWeek = 1, startTime = "08:00", endTime = "09:00"),
            )
            database.scheduleDao().insertSchedule(
                ScheduleEntity(usrId = secondUser, resourceId = resourceId, scheduleName = "Second", dayOfWeek = 2, startTime = "10:00", endTime = "11:00"),
            )

            assertEquals(0, database.scheduleDao().deleteSchedule(firstSchedule, secondUser))
            assertEquals(1, database.scheduleDao().getScheduleForUser(firstUser).size)
            assertEquals(1, database.scheduleDao().getScheduleForUser(secondUser).size)
        } finally {
            database.close()
        }
    }
}
