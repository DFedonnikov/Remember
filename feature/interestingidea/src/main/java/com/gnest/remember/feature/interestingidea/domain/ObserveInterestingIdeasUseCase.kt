package com.gnest.remember.feature.interestingidea.domain

import com.gnest.remember.core.note.Note
import com.gnest.remember.feature.interestingidea.data.InterestingIdeaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveInterestingIdeasUseCase : () -> Flow<List<Note.InterestingIdea>>

class ObserveInterestingIdeasUseCaseImpl @Inject constructor(private val repository: InterestingIdeaRepository) :
    ObserveInterestingIdeasUseCase {

    override fun invoke(): Flow<List<Note.InterestingIdea>> = repository.observeInterestingIdeas()
}