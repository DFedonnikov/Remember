package com.gnest.remember.feature.home.di

import com.gnest.remember.feature.home.navigation.HomeScreensProvider
import com.gnest.remember.feature.home.navigation.HomeScreensProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface HomeScreensModule {

    @Binds
    fun bindScreensProvider(provider: HomeScreensProviderImpl): HomeScreensProvider
}