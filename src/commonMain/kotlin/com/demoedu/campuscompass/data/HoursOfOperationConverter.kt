package com.demoedu.campuscompass.data

import androidx.room3.TypeConverter

/** Stores one entry per day, for example "Mon 08:00-17:00". */
class HoursOfOperationConverter {
    @TypeConverter
    fun fromHoursOfOperation(hours: List<String>): String =
        hours.joinToString(ENTRY_SEPARATOR)

    @TypeConverter
    fun toHoursOfOperation(value: String): List<String> =
        if (value.isEmpty()) emptyList() else value.split(ENTRY_SEPARATOR)

    private companion object {
        const val ENTRY_SEPARATOR = "\u001F"
    }
}
