package com.gnest.remember.core.database.util

import androidx.room.TypeConverter
import com.gnest.remember.core.note.NoteColor
import com.gnest.remember.core.note.RepeatPeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun fromMillis(value: Long?): LocalDateTime? =
        value?.let { Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault()) }

    @TypeConverter
    fun dateTimeToMillis(dateTime: LocalDateTime?): Long? =
        dateTime?.toInstant(TimeZone.currentSystemDefault())?.toEpochMilliseconds()

    @TypeConverter
    fun colorFromString(color: String?): NoteColor = runCatching { color?.let { NoteColor.valueOf(it) } }
        .getOrNull() ?: NoteColor.WHITE

    @TypeConverter
    fun colorToString(color: NoteColor?): String? = color?.name

    @TypeConverter
    fun repeatPeriodToString(period: RepeatPeriod): String = Json.encodeToString(period)

    @TypeConverter
    fun stringToRepeatPeriod(periodJson: String): RepeatPeriod {
        return Json.decodeFromString(periodJson)
    }

}