package com.gnest.remember.feature.notesettings.domain

import com.gnest.remember.core.note.NoteColor
import com.gnest.remember.feature.notesettings.data.NoteSettingsRepository
import javax.inject.Inject

internal interface SaveNoteColorUseCase : suspend (Long, NoteColor) -> Unit

internal class SaveNoteColorUseCaseImpl @Inject constructor(private val repository: NoteSettingsRepository) :
    SaveNoteColorUseCase {

    override suspend fun invoke(id: Long, color: NoteColor) {
        repository.saveNoteColor(id, color)
    }
}