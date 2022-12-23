package com.gnest.remember.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity
data class InterestingIdeaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "position") val position: Int,
    @ColumnInfo(name = "color") val color: NoteColor,
    @ColumnInfo(name = "alarm_date") val alarmDate: LocalDateTime?,
    @ColumnInfo(name = "is_alarm_set") val isAlarmSet: Boolean,
    @ColumnInfo(name = "is_finished") val isFinished: Boolean
)

@Entity
class ActiveNoPositionUpdate(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "color") val color: NoteColor,
    @ColumnInfo(name = "alarm_date") val alarmDate: LocalDateTime?,
    @ColumnInfo(name = "is_alarm_set") val isAlarmSet: Boolean
)