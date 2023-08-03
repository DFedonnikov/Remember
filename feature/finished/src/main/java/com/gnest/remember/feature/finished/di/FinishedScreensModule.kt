package com.gnest.remember.feature.finished.di

import com.gnest.remember.feature.finished.navigation.FinishedScreensProvider
import com.gnest.remember.feature.finished.navigation.FinishedScreensProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface FinishedScreensModule {

    @Binds
    fun provideScreensProvider(provider: FinishedScreensProviderImpl): FinishedScreensProvider
}