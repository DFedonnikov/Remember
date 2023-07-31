package com.gnest.remember.feature.reminder.domain

import com.gnest.remember.feature.reminder.data.ReminderRepository
import javax.inject.Inject

internal interface SaveInitialReminderDateUseCase : (Long) -> Unit

internal class SaveInitialReminderDateUseCaseImpl @Inject constructor(private val repository: ReminderRepository) : SaveInitialReminderDateUseCase {

    override fun invoke(id: Long) = repository.saveInitialReminderDate(id)
}