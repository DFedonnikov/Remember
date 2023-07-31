package com.gnest.remember.core.note

import android.os.Parcelable
import kotlinx.datetime.DayOfWeek
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
sealed interface RepeatPeriod: Parcelable {
    @Serializable
    @Parcelize
    object Once : RepeatPeriod
    @Serializable
    @Parcelize
    object Daily : RepeatPeriod
    @Serializable
    @Parcelize
    object Weekdays : RepeatPeriod
    @Serializable
    @Parcelize
    object Weekend : RepeatPeriod
    @Serializable
    @Parcelize
    data class Custom(val days: List<DayOfWeek>) : RepeatPeriod
}