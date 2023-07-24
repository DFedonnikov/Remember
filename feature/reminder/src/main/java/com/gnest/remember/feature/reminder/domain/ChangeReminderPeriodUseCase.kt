package com.gnest.remember.feature.reminder.domain

import com.gnest.remember.core.note.RepeatPeriod
import com.gnest.remember.feature.reminder.data.ReminderRepository
import javax.inject.Inject

internal interface ChangeReminderPeriodUseCase : (Long, RepeatPeriod) -> Unit

internal class ChangeReminderPeriodUseCaseImpl @Inject constructor(private val repository: ReminderRepository) : ChangeReminderPeriodUseCase {

    override fun invoke(id: Long, period: RepeatPeriod) {
        with(period) {
            if (this is RepeatPeriod.Custom && this.days.isEmpty()) {
                repository.saveNoteReminderRepeatPeriod(id, RepeatPeriod.Once)
            } else {
                repository.saveNoteReminderRepeatPeriod(id, period)
            }
        }
    }
}