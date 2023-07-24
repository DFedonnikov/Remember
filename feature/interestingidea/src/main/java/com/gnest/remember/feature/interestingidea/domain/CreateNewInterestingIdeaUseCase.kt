package com.gnest.remember.feature.interestingidea.domain

import com.gnest.remember.core.note.Note
import com.gnest.remember.feature.interestingidea.data.InterestingIdeaRepository
import javax.inject.Inject

interface CreateNewInterestingIdeaUseCase: suspend () -> Note.InterestingIdea

class CreateNewInterestingIdeaUseCaseImpl @Inject constructor(private val repository: InterestingIdeaRepository): CreateNewInterestingIdeaUseCase {

    override suspend fun invoke(): Note.InterestingIdea = repository.createNewIdea()
}