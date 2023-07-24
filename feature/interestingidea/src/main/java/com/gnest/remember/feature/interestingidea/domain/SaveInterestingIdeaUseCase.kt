package com.gnest.remember.feature.interestingidea.domain

import com.gnest.remember.core.note.Note
import com.gnest.remember.feature.interestingidea.data.InterestingIdeaRepository
import javax.inject.Inject

interface SaveInterestingIdeaUseCase: suspend (Note.InterestingIdea) -> Unit

class SaveInterestingIdeaUseCaseImpl @Inject constructor(private val repository: InterestingIdeaRepository): SaveInterestingIdeaUseCase {

    override suspend fun invoke(idea: Note.InterestingIdea) = repository.updateIdea(idea)
}