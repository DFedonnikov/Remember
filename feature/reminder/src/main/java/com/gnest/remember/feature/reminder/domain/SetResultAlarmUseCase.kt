package com.gnest.remember.feature.reminder.domain

import com.gnest.remember.feature.reminder.alarm.AlarmClock
import com.gnest.remember.feature.reminder.alarm.AlarmInfo
import com.gnest.remember.feature.reminder.data.ReminderRepository
import javax.inject.Inject

interface SetResultAlarmUseCase : suspend (Long) -> Unit

class SetResultAlarmUseCaseImpl @Inject constructor(
    private val cancelInitialAlarmUseCase: CancelInitialAlarmUseCase,
    private val alarmClock: AlarmClock,
    private val repository: ReminderRepository,
) : SetResultAlarmUseCase {

    override suspend fun invoke(id: Long) {
        repository.getReminderInfo(id)?.let { reminderInfo ->
            reminderInfo.date?.let {
                cancelInitialAlarmUseCase(id)
                alarmClock.setAlarm(
                    AlarmInfo(
                        id = id,
                        dateTime = reminderInfo.date,
                        period = reminderInfo.repeatPeriod,
                        text = reminderInfo.noteTitle
                    )
                )
            }
        }
    }
}