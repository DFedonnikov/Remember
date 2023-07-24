package com.gnest.remember.feature.notesettings.domain

import com.gnest.remember.feature.notesettings.data.NoteSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

internal interface ObserveNoteReminderDateUseCase : (Long) -> Flow<LocalDateTime?>

internal class ObserveNoteReminderDateUseCaseImpl @Inject constructor(private val repository: NoteSettingsRepository) : ObserveNoteReminderDateUseCase {

    override fun invoke(id: Long): Flow<LocalDateTime?> = repository.observeNoteReminderDate(id)
}