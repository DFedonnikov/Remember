package com.gnest.remember.feature.reminder

import android.os.Build
import kotlinx.datetime.DayOfWeek
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

fun DayOfWeek.displayName(): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    getDisplayName(TextStyle.SHORT, Locale.getDefault())
} else {
    with(Calendar.getInstance()) {
        set(Calendar.DAY_OF_WEEK, ordinal + 1)
        "%ta".format(this)
    }
}