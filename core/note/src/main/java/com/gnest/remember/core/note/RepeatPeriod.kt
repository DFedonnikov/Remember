package com.gnest.remember.core.note

import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable

@Serializable
sealed interface RepeatPeriod {
    @Serializable
    object Once : RepeatPeriod
    @Serializable
    object Daily : RepeatPeriod
    @Serializable
    object Weekdays : RepeatPeriod
    @Serializable
    object Weekend : RepeatPeriod
    @Serializable
    data class Custom(val days: List<DayOfWeek>) : RepeatPeriod
}