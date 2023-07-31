package com.gnest.remember.feature.interestingidea.domain

import com.gnest.remember.core.note.Note
import com.gnest.remember.feature.interestingidea.data.InterestingIdeaRepository
import javax.inject.Inject

interface GetInterestingIdeaUseCase : suspend (Long) -> Note.InterestingIdea?

class GetInterestingIdeaUseCaseImpl @Inject constructor(private val repository: InterestingIdeaRepository) :
    GetInterestingIdeaUseCase {

    override suspend fun invoke(id: Long): Note.InterestingIdea? {
        return repository.getIdeaById(id)
    }
}