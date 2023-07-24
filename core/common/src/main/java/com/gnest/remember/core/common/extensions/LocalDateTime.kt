package com.gnest.remember.core.common.extensions

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

fun LocalDateTime.formatForNewNote(): String = when {
    isToday() -> "${padded { hour }}.${padded { minute }}"
    else -> "${padded { dayOfMonth }}.${padded { monthNumber }}.$year " +
            "${padded { hour }}:${padded { minute }}"
}

fun LocalDateTime.isToday() =
    with(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date) {
        this == this@isToday.date
    }

fun Clock.System.localDateTimeNow() = now().toLocalDateTime(TimeZone.currentSystemDefault())

fun Clock.System.nearestRoundHour() = with(now()) {
    val minutes = toLocalDateTime(TimeZone.currentSystemDefault()).minute
    this.plus(1.hours).minus(minutes.minutes).toLocalDateTime(TimeZone.currentSystemDefault())
}

private inline fun LocalDateTime.padded(block: LocalDateTime.() -> Int) =
    block().toString().padStart(2, '0')

fun LocalDateTime.formatForDate(): String {
    return "${padded { dayOfMonth }}.${padded { monthNumber }}.$year"
}

fun LocalDateTime.formatForTime(): String {
    return "${padded { hour }}:${padded { minute }}"
}

fun LocalDateTime.formatForDateTime(): String {
    return "${padded { dayOfMonth }}.${padded { monthNumber }}.$year ${padded { hour }}:${padded { minute }}"
}