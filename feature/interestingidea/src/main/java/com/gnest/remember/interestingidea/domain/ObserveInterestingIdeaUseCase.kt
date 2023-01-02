package com.gnest.remember.interestingidea.domain

import com.gnest.remember.interestingidea.data.InterestingIdeaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveInterestingIdeaUseCase : (Long) -> Flow<InterestingIdea>

class ObserveInterestingIdeaUseCaseImpl @Inject constructor(private val repository: InterestingIdeaRepository) :
    ObserveInterestingIdeaUseCase {

    override fun invoke(id: Long): Flow<InterestingIdea> = repository.observeIdeaById(id)
}