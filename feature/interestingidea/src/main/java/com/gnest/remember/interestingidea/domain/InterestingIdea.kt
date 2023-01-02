package com.gnest.remember.interestingidea.domain

import com.gnest.remember.common.domain.NoteColor
import kotlinx.datetime.LocalDateTime

data class InterestingIdea(
    val id: Long,
    val title: String,
    val text: String,
    val color: NoteColor,
    val lastEdited: LocalDateTime,
    val alarmDate: LocalDateTime?,
    val isAlarmSet: Boolean)