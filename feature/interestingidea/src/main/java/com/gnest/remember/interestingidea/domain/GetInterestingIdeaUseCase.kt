package com.gnest.remember.interestingidea.domain

import com.gnest.remember.interestingidea.data.InterestingIdeaRepository
import javax.inject.Inject

interface GetInterestingIdeaUseCase : suspend (Long) -> InterestingIdea?

class GetInterestingIdeaUseCaseImpl @Inject constructor(private val repository: InterestingIdeaRepository) :
    GetInterestingIdeaUseCase {

    override suspend fun invoke(id: Long): InterestingIdea? = repository.getIdeaById(id)
}