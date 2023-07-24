package com.gnest.remember.feature.interestingidea.domain

import com.gnest.remember.core.note.Note
import com.gnest.remember.feature.interestingidea.data.InterestingIdeaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveInterestingIdeaUseCase : (Long) -> Flow<Note.InterestingIdea>

class ObserveInterestingIdeaUseCaseImpl @Inject constructor(private val repository: InterestingIdeaRepository) :
    ObserveInterestingIdeaUseCase {

    override fun invoke(id: Long): Flow<Note.InterestingIdea> = repository.observeIdeaById(id)
}