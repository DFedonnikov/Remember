package com.gnest.remember.interestingidea.domain

import com.gnest.remember.interestingidea.data.InterestingIdeaRepository
import javax.inject.Inject

interface DeleteInterestingIdeaUseCase : (Long) -> Unit

class DeleteInterestingIdeaUseCaseImpl @Inject constructor(private val repository: InterestingIdeaRepository) :
    DeleteInterestingIdeaUseCase {

    override fun invoke(id: Long) {
        repository.deleteById(id)
    }
}