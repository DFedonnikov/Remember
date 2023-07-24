package com.gnest.remember.core.note

import kotlinx.datetime.LocalDateTime

sealed interface Note {

    val id: Long
    val title: String
    val text: String
    val color: NoteColor
    val lastEdited: LocalDateTime
    val alarmDate: LocalDateTime?
    val isAlarmSet: Boolean
    val repeatPeriod: RepeatPeriod

    data class InterestingIdea(
        override val id: Long,
        override val title: String,
        override val text: String,
        override val color: NoteColor,
        override val lastEdited: LocalDateTime,
        override val alarmDate: LocalDateTime?,
        override val isAlarmSet: Boolean,
        override val repeatPeriod: RepeatPeriod
    ) : Note

}