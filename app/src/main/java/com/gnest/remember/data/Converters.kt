package com.gnest.remember.data

import androidx.room.TypeConverter
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

class Converters {

    private val timeZone = DateTimeZone.getDefault()

    @TypeConverter
    fun fromTimeStamp(timeStamp: String?): DateTime? = timeStamp?.let { DateTime.parse(it) }

    @TypeConverter
    fun dateToTimeStamp(date: DateTime?): String? = date?.toString()
}