package com.gnest.remember.feature.reminder.di

import com.gnest.remember.feature.reminder.navigation.ScreensProvider
import com.gnest.remember.feature.reminder.navigation.ScreensProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface ReminderScreensModule {

    @Binds
    fun bindScreensProvider(provider: ScreensProviderImpl): ScreensProvider
}