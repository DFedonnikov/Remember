package com.gnest.remember.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity
data class Memo(@PrimaryKey val id: Int,
                @ColumnInfo(name = "text") val text: String,
                @ColumnInfo(name = "position") val position: Int,
                @ColumnInfo(name = "color") val color: MemoColor,
                @ColumnInfo(name = "alarm_date") val alarmDate: LocalDateTime?,
                @ColumnInfo(name = "is_alarm_set") val isAlarmSet: Boolean,
                @ColumnInfo(name = "is_archived") val isArchived: Boolean)

enum class MemoColor {
    YELLOW,
    BLUE,
    EMERALD,
    PURPLE
}