package com.gnest.remember.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity
data class MemoDTO(@PrimaryKey(autoGenerate = true) val id: Int = 0,
                   @ColumnInfo(name = "text") val text: String,
                   @ColumnInfo(name = "position") val position: Int,
                   @ColumnInfo(name = "color") val color: String,
                   @ColumnInfo(name = "alarmDate") val dateTime: DateTime?,
                   @ColumnInfo(name = "isArchived") val isArchived: Boolean)