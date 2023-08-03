package com.gnest.remember.feature.reminder.di

import com.gnest.remember.feature.reminder.navigation.ReminderScreensProvider
import com.gnest.remember.feature.reminder.navigation.ReminderScreensProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface ReminderScreensModule {

    @Binds
    fun bindScreensProvider(provider: ReminderScreensProviderImpl): ReminderScreensProvider
}