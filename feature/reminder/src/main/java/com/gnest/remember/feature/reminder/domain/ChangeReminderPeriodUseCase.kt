package com.gnest.remember.feature.reminder.domain

import com.gnest.remember.core.note.RepeatPeriod
import com.gnest.remember.feature.reminder.data.ReminderRepository
import javax.inject.Inject

internal interface ChangeReminderPeriodUseCase : suspend (Long, RepeatPeriod) -> Unit

internal class ChangeReminderPeriodUseCaseImpl @Inject constructor(
    private val repository: ReminderRepository
) : ChangeReminderPeriodUseCase{

    override suspend fun invoke(id: Long, period: RepeatPeriod) {
        with(period) {
            val repeatPeriod = when {
                this is RepeatPeriod.Custom && this.days.isEmpty() -> RepeatPeriod.Once
                else -> period
            }
            repository.saveNoteReminderRepeatPeriod(id, repeatPeriod)
        }
    }
}