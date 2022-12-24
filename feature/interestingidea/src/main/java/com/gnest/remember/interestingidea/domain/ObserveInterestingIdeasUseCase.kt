package com.gnest.remember.interestingidea.domain

import com.gnest.remember.interestingidea.data.InterestingIdeaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveInterestingIdeasUseCase : () -> Flow<List<InterestingIdea>>

class ObserveInterestingIdeasUseCaseImpl @Inject constructor(private val repository: InterestingIdeaRepository) :
    ObserveInterestingIdeasUseCase {

    override fun invoke(): Flow<List<InterestingIdea>> = repository.observeInterestingIdeas()
}