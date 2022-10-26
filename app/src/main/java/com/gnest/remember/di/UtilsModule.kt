package com.gnest.remember.di

import com.gnest.remember.utils.AppDispatchers
import com.gnest.remember.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UtilsModule {

    @Provides
    fun provideDispatchers(): DispatcherProvider = AppDispatchers()
}