package com.gnest.remember.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gnest.remember.core.note.NoteColor
import com.gnest.remember.core.note.RepeatPeriod
import kotlinx.datetime.LocalDateTime

@Entity
data class InterestingIdeaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "position") val position: Int,
    @ColumnInfo(name = "color") val color: NoteColor,
    @ColumnInfo(name = "last_edited") val lastEdited: LocalDateTime,
    @ColumnInfo(name = "reminder_date") val reminderDate: LocalDateTime?,
    @ColumnInfo(name = "is_reminder_set") val isReminderSet: Boolean,
    @ColumnInfo(name = "repeat_period") val repeatPeriod: RepeatPeriod,
    @ColumnInfo(name = "is_finished") val isFinished: Boolean
)

@Entity
class ActiveNoPositionUpdate(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "color") val color: NoteColor,
    @ColumnInfo(name = "last_edited") val lastEdited: LocalDateTime,
    @ColumnInfo(name = "reminder_date") val reminderDate: LocalDateTime?,
    @ColumnInfo(name = "is_reminder_set") val isReminderSet: Boolean
)

@Entity
class ColorUpdate(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "color") val color: NoteColor,
    @ColumnInfo(name = "last_edited") val lastEdited: LocalDateTime
)

@Entity
class ReminderUpdate(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "reminder_date") val reminderDate: LocalDateTime?,
    @ColumnInfo(name = "last_edited") val lastEdited: LocalDateTime
)

@Entity
class RepeatPeriodUpdate(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "repeat_period") val period: RepeatPeriod,
    @ColumnInfo(name = "last_edited") val lastEdited: LocalDateTime
)