package com.gnest.remember.feature.interestingidea.di

import com.gnest.remember.feature.interestingidea.navigation.ScreensProvider
import com.gnest.remember.feature.interestingidea.navigation.ScreensProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface InterestingIdeaScreensModule {

    @Binds
    fun bindScreensProvider(provider: ScreensProviderImpl): ScreensProvider
}