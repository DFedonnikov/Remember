package com.gnest.remember.feature.reminder.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.gnest.remember.core.note.RepeatPeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

interface AlarmClock {

    fun setAlarm(id: Long, dateTime: LocalDateTime, period: RepeatPeriod)

}

class AlarmClockImpl(private val context: Context) : AlarmClock {

    private val alarmManager by lazy { context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }


    @SuppressLint("MissingPermission")
    override fun setAlarm(id: Long, dateTime: LocalDateTime, period: RepeatPeriod) {
        val intent = with(Intent(context, AlarmReceiver::class.java)) {
            putExtra("NOTE_ID", id)
            PendingIntent.getBroadcast(context, 0, this, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val startMillis = dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        when (period) {
            RepeatPeriod.Once -> alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                startMillis,
                intent
            )
            RepeatPeriod.Daily -> {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    startMillis,
                    DAILY_INTERVAL,
                    intent
                )
            }
            RepeatPeriod.Weekdays -> {

            }

            RepeatPeriod.Weekend -> TODO()
            is RepeatPeriod.Custom -> TODO()
        }

    }

    companion object {

        private const val DAILY_INTERVAL: Long = 1000 * 60 * 60 * 24
    }
}