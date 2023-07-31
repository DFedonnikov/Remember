package com.gnest.remember.feature.reminder.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.gnest.remember.core.note.RepeatPeriod
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import javax.inject.Inject

interface AlarmClock {

    fun setAlarm(alarmInfo: AlarmInfo)
    fun cancelAlarm(id: Long, repeatPeriod: RepeatPeriod)

}

class AlarmClockImpl @Inject constructor(@ApplicationContext private val context: Context) : AlarmClock {

    private val alarmManager by lazy { context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    @SuppressLint("MissingPermission")
    override fun setAlarm(alarmInfo: AlarmInfo) {
        val time = with(alarmInfo.dateTime.time) {
            LocalTime(hour, minute, 0)
        }
        val localDateTime = LocalDateTime(alarmInfo.dateTime.date, time)
        when (alarmInfo.period) {
            RepeatPeriod.Once -> setOneTimeAlarm(alarmInfo, localDateTime)
            RepeatPeriod.Daily -> setAlarmForDaysOfWeek(alarmInfo, localDateTime, WEEK)
            RepeatPeriod.Weekdays -> setAlarmForDaysOfWeek(alarmInfo, localDateTime, WEEKDAYS)
            RepeatPeriod.Weekend -> setAlarmForDaysOfWeek(alarmInfo, localDateTime, WEEKEND)
            is RepeatPeriod.Custom -> setAlarmForDaysOfWeek(alarmInfo, localDateTime, alarmInfo.period.days.toSet())
        }
    }

    private fun getPendingIntent(alarmInfo: AlarmInfo, intentIdentifier: Any): PendingIntent {
        val intent = with(Intent(context, AlarmReceiver::class.java)) {
            action = getIdentifier(alarmInfo.id, intentIdentifier)
            putExtra(ALARM_INFO_KEY, alarmInfo)
            PendingIntent.getBroadcast(context, 0, this, PendingIntent.FLAG_UPDATE_CURRENT.or(PendingIntent.FLAG_IMMUTABLE))
        }
        return intent
    }

    private fun getIdentifier(id: Long, identifierArgument: Any) =
        "$INTENT_ACTION $id $identifierArgument"

    private fun setOneTimeAlarm(alarmInfo: AlarmInfo, dateTime: LocalDateTime) {
        val intent = getPendingIntent(alarmInfo, "")
        val startMillis = dateTime
            .toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            startMillis,
            intent
        )
    }

    private fun setAlarmForDaysOfWeek(alarmInfo: AlarmInfo, dateTime: LocalDateTime, validWeekDays: Set<DayOfWeek>) {
        var currentDateTime = dateTime
        repeat(7) {
            val date = currentDateTime.date
            if (date.dayOfWeek in validWeekDays) {
                val intent = getPendingIntent(alarmInfo, date.dayOfWeek.hashCode())
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    currentDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
                    INTERVAL_WEEK,
                    intent
                )
            }
            currentDateTime = LocalDateTime(currentDateTime.date.plus(DatePeriod(days = 1)), currentDateTime.time)
        }
    }

    override fun cancelAlarm(id: Long, repeatPeriod: RepeatPeriod) {
        getCancelIntents(id, repeatPeriod).forEach { alarmManager.cancel(it) }
    }

    private fun getCancelIntent(id: Long, identifierArgument: Any): PendingIntent = with(Intent(context, AlarmReceiver::class.java)) {
        val identifier = getIdentifier(id, identifierArgument)
        action = identifier
        PendingIntent.getBroadcast(context, 0, this, PendingIntent.FLAG_UPDATE_CURRENT.or(PendingIntent.FLAG_IMMUTABLE))
    }

    private fun getCancelIntents(id: Long, repeatPeriod: RepeatPeriod): List<PendingIntent> = when (repeatPeriod) {
        RepeatPeriod.Once -> listOf(getCancelIntent(id, ""))
        RepeatPeriod.Daily -> DayOfWeek.values().toList().mapToCancelIntent(id)
        RepeatPeriod.Weekdays -> DayOfWeek.values().take(5).mapToCancelIntent(id)
        RepeatPeriod.Weekend -> DayOfWeek.values().takeLast(2).mapToCancelIntent(id)
        is RepeatPeriod.Custom -> repeatPeriod.days.mapToCancelIntent(id)
    }

    private fun List<DayOfWeek>.mapToCancelIntent(id: Long) = map { getCancelIntent(id, it.hashCode()) }

    companion object {

        internal const val ALARM_INFO_KEY = "ALARM_INFO_KEY"
        private const val INTENT_ACTION = "INTENT_ACTION"
        private const val INTERVAL_WEEK = AlarmManager.INTERVAL_DAY * 7
        private val WEEKDAYS by lazy { DayOfWeek.values().take(5).toSet() }
        private val WEEKEND by lazy { DayOfWeek.values().takeLast(2).toSet() }
        private val WEEK by lazy { DayOfWeek.values().toSet() }
    }
}