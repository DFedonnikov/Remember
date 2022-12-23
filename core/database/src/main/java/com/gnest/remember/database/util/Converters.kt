package com.gnest.remember.database.util

import androidx.room.TypeConverter
import com.gnest.remember.database.model.NoteColor
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

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
}