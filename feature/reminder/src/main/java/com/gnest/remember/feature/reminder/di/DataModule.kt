package com.gnest.remember.feature.reminder.di

import com.gnest.remember.feature.reminder.alarm.AlarmClock
import com.gnest.remember.feature.reminder.alarm.AlarmClockImpl
import com.gnest.remember.feature.reminder.data.LocalDataSource
import com.gnest.remember.feature.reminder.data.LocalDataSourceImpl
import com.gnest.remember.feature.reminder.data.ReminderRepository
import com.gnest.remember.feature.reminder.data.ReminderRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {

    @Binds
    @Singleton
    fun bindReminderRepository(repository: ReminderRepositoryImpl): ReminderRepository

    @Binds
    @Singleton
    fun bindLocalDataSource(dataSource: LocalDataSourceImpl): LocalDataSource

    @Binds
    fun bindAlarmClock(alarmClockImpl: AlarmClockImpl): AlarmClock
}