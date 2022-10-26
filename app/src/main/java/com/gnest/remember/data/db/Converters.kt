package com.gnest.remember.data.db

import androidx.room.TypeConverter
import com.gnest.remember.data.db.entity.MemoColor
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
    fun colorFromString(color: String?): MemoColor = runCatching { color?.let { MemoColor.valueOf(it) } }
            .getOrNull() ?: MemoColor.YELLOW

    @TypeConverter
    fun colorToString(color: MemoColor?): String? = color?.name
}