package com.gnest.remember.feature.reminder.domain

import com.gnest.remember.feature.reminder.data.ReminderRepository
import javax.inject.Inject

interface RestoreInitialReminderUseCase : suspend (Long) -> Unit

class RestoreInitialReminderUseCaseImpl @Inject constructor(
    private val repository: ReminderRepository
) : RestoreInitialReminderUseCase {

    override suspend fun invoke(id: Long) = repository.restoreInitialReminder(id)
}