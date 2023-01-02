package com.gnest.remember.interestingidea.di

import com.gnest.remember.interestingidea.domain.ObserveInterestingIdeasUseCase
import com.gnest.remember.interestingidea.domain.ObserveInterestingIdeasUseCaseImpl
import com.gnest.remember.interestingidea.domain.CreateNewInterestingIdeaUseCase
import com.gnest.remember.interestingidea.domain.CreateNewInterestingIdeaUseCaseImpl
import com.gnest.remember.interestingidea.domain.DeleteInterestingIdeaUseCase
import com.gnest.remember.interestingidea.domain.DeleteInterestingIdeaUseCaseImpl
import com.gnest.remember.interestingidea.domain.GetInterestingIdeaUseCase
import com.gnest.remember.interestingidea.domain.GetInterestingIdeaUseCaseImpl
import com.gnest.remember.interestingidea.domain.ObserveInterestingIdeaUseCase
import com.gnest.remember.interestingidea.domain.ObserveInterestingIdeaUseCaseImpl
import com.gnest.remember.interestingidea.domain.SaveInterestingIdeaUseCase
import com.gnest.remember.interestingidea.domain.SaveInterestingIdeaUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface UseCaseModule {

    @Binds
    fun bindGetInterestingIdeaUseCase(useCase: GetInterestingIdeaUseCaseImpl): GetInterestingIdeaUseCase

    @Binds
    fun bindObserveInterestingIdeaUseCase(useCase: ObserveInterestingIdeaUseCaseImpl): ObserveInterestingIdeaUseCase

    @Binds
    fun bindCreateNewInterestingIdeaUseCase(useCase: CreateNewInterestingIdeaUseCaseImpl): CreateNewInterestingIdeaUseCase

    @Binds
    fun bindSaveInterestingIdeaUseCase(useCase: SaveInterestingIdeaUseCaseImpl): SaveInterestingIdeaUseCase

    @Binds
    fun bindDeleteInterestingIdeaUseCase(useCase: DeleteInterestingIdeaUseCaseImpl): DeleteInterestingIdeaUseCase

    @Binds
    fun bindObserveInterestingIdeasUseCase(useCase: ObserveInterestingIdeasUseCaseImpl): ObserveInterestingIdeasUseCase
}