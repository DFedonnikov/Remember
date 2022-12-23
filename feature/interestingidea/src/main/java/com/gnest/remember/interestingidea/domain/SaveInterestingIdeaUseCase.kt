package com.gnest.remember.interestingidea.domain

import com.gnest.remember.interestingidea.data.InterestingIdeaRepository
import javax.inject.Inject

interface SaveInterestingIdeaUseCase: suspend (InterestingIdea) -> Unit

class SaveInterestingIdeaUseCaseImpl @Inject constructor(private val repository: InterestingIdeaRepository): SaveInterestingIdeaUseCase {

    override suspend fun invoke(idea: InterestingIdea) = repository.updateIdea(idea)
}