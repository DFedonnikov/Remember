package com.gnest.remember.interestingidea.di

import com.gnest.remember.interestingidea.data.InterestingIdeaRepository
import com.gnest.remember.interestingidea.data.InterestingIdeaRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindRepository(repository: InterestingIdeaRepositoryImpl): InterestingIdeaRepository
}