package com.gnest.remember.feature.reminder.domain

import com.gnest.remember.feature.reminder.data.ReminderRepository
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

internal interface ChangeReminderDateUseCase : (Long, LocalDateTime?) -> Unit

internal class ChangeReminderDateUseCaseImpl @Inject constructor(private val repository: ReminderRepository) : ChangeReminderDateUseCase {

    override fun invoke(id: Long, date: LocalDateTime?) = repository.saveNoteReminderDate(id, date)
}