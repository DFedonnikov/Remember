package com.gnest.remember.notesettings.domain

import com.gnest.remember.common.domain.NoteColor
import com.gnest.remember.notesettings.data.NoteSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveNoteColorUseCase : (Long) -> Flow<NoteColor>

class ObserveNoteColorUseCaseImpl @Inject constructor(private val repository: NoteSettingsRepository) :
    ObserveNoteColorUseCase {

    override fun invoke(id: Long): Flow<NoteColor> {
        return repository.observeInterestingIdea(id)
    }
}