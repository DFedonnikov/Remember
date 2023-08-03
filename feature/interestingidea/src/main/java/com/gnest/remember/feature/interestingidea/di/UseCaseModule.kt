package com.gnest.remember.feature.interestingidea.di

import com.gnest.remember.feature.interestingidea.domain.ObserveInterestingIdeasUseCase
import com.gnest.remember.feature.interestingidea.domain.ObserveInterestingIdeasUseCaseImpl
import com.gnest.remember.feature.interestingidea.domain.CreateNewInterestingIdeaUseCase
import com.gnest.remember.feature.interestingidea.domain.CreateNewInterestingIdeaUseCaseImpl
import com.gnest.remember.feature.interestingidea.domain.DeleteInterestingIdeaUseCase
import com.gnest.remember.feature.interestingidea.domain.DeleteInterestingIdeaUseCaseImpl
import com.gnest.remember.feature.interestingidea.domain.GetInterestingIdeaUseCase
import com.gnest.remember.feature.interestingidea.domain.GetInterestingIdeaUseCaseImpl
import com.gnest.remember.feature.interestingidea.domain.SaveInterestingIdeaUseCase
import com.gnest.remember.feature.interestingidea.domain.SaveInterestingIdeaUseCaseImpl
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
    fun bindCreateNewInterestingIdeaUseCase(useCase: CreateNewInterestingIdeaUseCaseImpl): CreateNewInterestingIdeaUseCase

    @Binds
    fun bindSaveInterestingIdeaUseCase(useCase: SaveInterestingIdeaUseCaseImpl): SaveInterestingIdeaUseCase

    @Binds
    fun bindDeleteInterestingIdeaUseCase(useCase: DeleteInterestingIdeaUseCaseImpl): DeleteInterestingIdeaUseCase

    @Binds
    fun bindObserveInterestingIdeasUseCase(useCase: ObserveInterestingIdeasUseCaseImpl): ObserveInterestingIdeasUseCase
}