package com.gnest.remember.feature.search.di

import com.gnest.remember.feature.search.navigation.SearchScreensProvider
import com.gnest.remember.feature.search.navigation.SearchScreensProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface SearchScreensModule {

    @Binds
    fun bindScreensProvider(provider: SearchScreensProviderImpl): SearchScreensProvider
}