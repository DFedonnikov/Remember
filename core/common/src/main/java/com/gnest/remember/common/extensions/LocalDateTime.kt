package com.gnest.remember.common.extensions

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun LocalDateTime.formatForNewNote(): String {
    this.toInstant(TimeZone.currentSystemDefault())
    return when {
        isToday() -> "${padded { hour }}.${padded { minute }}"
        else -> "${padded { dayOfMonth }}.${padded { monthNumber }}.$year " +
                "${padded { hour }}:${padded { minute }}"
    }
}

fun LocalDateTime.isToday() =
    with(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date) {
        this == this@isToday.date
    }

fun Clock.System.localDateTimeNow() = now().toLocalDateTime(TimeZone.currentSystemDefault())

private inline fun LocalDateTime.padded(block: LocalDateTime.() -> Int) =
    block().toString().padStart(2, '0')