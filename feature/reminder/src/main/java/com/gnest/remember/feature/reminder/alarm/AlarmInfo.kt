package com.gnest.remember.feature.reminder.alarm

import android.os.Parcelable
import androidx.core.app.NotificationCompat
import com.gnest.remember.core.common.extensions.localDateTimeNow
import com.gnest.remember.core.note.RepeatPeriod
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlarmInfo(
    val id: Long,
    @IgnoredOnParcel
    val dateTime: LocalDateTime = Clock.System.localDateTimeNow(),
    val period: RepeatPeriod,
    val text: String?,
    val priority: Int = NotificationCompat.PRIORITY_MAX,
    val autoCancel: Boolean = true,
    val isFullscreen: Boolean = true
) : Parcelable