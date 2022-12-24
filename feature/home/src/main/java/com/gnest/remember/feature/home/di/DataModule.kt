package com.gnest.remember.feature.home.di

import com.gnest.remember.feature.home.data.HomeRepository
import com.gnest.remember.feature.home.data.HomeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindHomeRepository(repository: HomeRepositoryImpl): HomeRepository
}