package com.gnest.remember.feature.notesettings.domain

import com.gnest.remember.core.note.NoteColor
import com.gnest.remember.feature.notesettings.data.NoteSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal interface ObserveNoteColorUseCase : (Long) -> Flow<NoteColor>

internal class ObserveNoteColorUseCaseImpl @Inject constructor(private val repository: NoteSettingsRepository) :
    ObserveNoteColorUseCase {

    override fun invoke(id: Long): Flow<NoteColor> {
        return repository.observeInterestingIdeaColor(id)
    }
}