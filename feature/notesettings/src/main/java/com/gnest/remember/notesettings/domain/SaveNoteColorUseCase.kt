package com.gnest.remember.notesettings.domain

import com.gnest.remember.common.domain.NoteColor
import com.gnest.remember.notesettings.data.NoteSettingsRepository
import javax.inject.Inject

interface SaveNoteColorUseCase : suspend (Long, NoteColor) -> Unit

class SaveNoteColorUseCaseImpl @Inject constructor(private val repository: NoteSettingsRepository) :
    SaveNoteColorUseCase {

    override suspend fun invoke(id: Long, color: NoteColor) {
        repository.saveNoteColor(id, color)
    }
}