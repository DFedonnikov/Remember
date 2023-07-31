package com.gnest.remember.feature.reminder.domain

import com.gnest.remember.core.note.RepeatPeriod
import kotlinx.datetime.LocalDateTime

data class ReminderInfo(val date: LocalDateTime?, val repeatPeriod: RepeatPeriod, val noteTitle: String)