package com.gnest.remember.interestingidea.domain

import com.gnest.remember.interestingidea.data.InterestingIdeaRepository
import javax.inject.Inject

interface CreateNewInterestingIdeaUseCase: suspend () -> InterestingIdea

class CreateNewInterestingIdeaUseCaseImpl @Inject constructor(private val repository: InterestingIdeaRepository): CreateNewInterestingIdeaUseCase {

    override suspend fun invoke(): InterestingIdea = repository.createNewIdea()
}