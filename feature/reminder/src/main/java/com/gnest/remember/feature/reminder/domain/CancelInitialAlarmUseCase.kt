package com.gnest.remember.feature.reminder.domain

import com.gnest.remember.feature.reminder.alarm.AlarmClock
import com.gnest.remember.feature.reminder.data.ReminderRepository
import javax.inject.Inject

interface CancelInitialAlarmUseCase : suspend (Long) -> Unit

class CancelInitialAlarmUseCaseImpl @Inject constructor(
    private val alarmClock: AlarmClock,
    private val repository: ReminderRepository
) : CancelInitialAlarmUseCase {

    override suspend fun invoke(id: Long) {
        alarmClock.cancelAlarm(id, repository.getInitialRepeatPeriod())
    }
}