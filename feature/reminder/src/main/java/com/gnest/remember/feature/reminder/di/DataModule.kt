package com.gnest.remember.feature.reminder.di

import com.gnest.remember.feature.reminder.data.ReminderRepository
import com.gnest.remember.feature.reminder.data.ReminderRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {

    @Binds
    fun bindReminderRepository(repository: ReminderRepositoryImpl): ReminderRepository
}