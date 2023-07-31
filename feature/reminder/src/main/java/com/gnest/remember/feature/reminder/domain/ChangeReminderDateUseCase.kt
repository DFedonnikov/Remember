package com.gnest.remember.feature.reminder.domain

import com.gnest.remember.core.common.extensions.localDateTimeNow
import com.gnest.remember.feature.reminder.data.ReminderRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import javax.inject.Inject

internal interface ChangeReminderDateUseCase {

    suspend fun changeReminderDateTime(id: Long, dateTime: LocalDateTime?)
    suspend fun changeReminderDate(id: Long, date: LocalDate)
    suspend fun changeReminderTime(id: Long, time: LocalTime): Boolean
}

internal class ChangeReminderDateUseCaseImpl @Inject constructor(private val repository: ReminderRepository) : ChangeReminderDateUseCase {

    override suspend fun changeReminderDateTime(id: Long, dateTime: LocalDateTime?) = repository.saveNoteReminderDate(id, dateTime)

    override suspend fun changeReminderDate(id: Long, date: LocalDate) {
        repository.getDateTime(id)?.let { dateTime ->
            changeReminderDateTime(id, LocalDateTime(date, dateTime.time))
        }
    }

    override suspend fun changeReminderTime(id: Long, time: LocalTime): Boolean = repository.getDateTime(id)?.let { dateTime ->
        val newDateTime = LocalDateTime(dateTime.date, time)
        val isValid = newDateTime > Clock.System.localDateTimeNow()
        if (isValid) {
            changeReminderDateTime(id, newDateTime)
        }
        isValid
    } ?: false
}