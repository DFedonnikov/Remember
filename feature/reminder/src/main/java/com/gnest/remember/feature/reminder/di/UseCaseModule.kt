package com.gnest.remember.feature.reminder.di

import com.gnest.remember.feature.reminder.domain.CancelInitialAlarmUseCase
import com.gnest.remember.feature.reminder.domain.CancelInitialAlarmUseCaseImpl
import com.gnest.remember.feature.reminder.domain.ChangeReminderDateUseCase
import com.gnest.remember.feature.reminder.domain.ChangeReminderDateUseCaseImpl
import com.gnest.remember.feature.reminder.domain.SaveInitialReminderDateUseCase
import com.gnest.remember.feature.reminder.domain.SaveInitialReminderDateUseCaseImpl
import com.gnest.remember.feature.reminder.domain.ChangeReminderPeriodUseCase
import com.gnest.remember.feature.reminder.domain.ChangeReminderPeriodUseCaseImpl
import com.gnest.remember.feature.reminder.domain.ObserveNoteReminderInfoUseCase
import com.gnest.remember.feature.reminder.domain.ObserveNoteReminderInfoUseCaseImpl
import com.gnest.remember.feature.reminder.domain.ObserveCustomPeriodUseCase
import com.gnest.remember.feature.reminder.domain.ObserveCustomPeriodUseCaseImpl
import com.gnest.remember.feature.reminder.domain.RestoreInitialReminderUseCase
import com.gnest.remember.feature.reminder.domain.RestoreInitialReminderUseCaseImpl
import com.gnest.remember.feature.reminder.domain.SetResultAlarmUseCase
import com.gnest.remember.feature.reminder.domain.SetResultAlarmUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface UseCaseModule {

    @Binds
    fun bindObserveNoteReminderInfoUseCase(useCase: ObserveNoteReminderInfoUseCaseImpl): ObserveNoteReminderInfoUseCase


    @Binds
    fun bindChangeReminderDateUseCase(useCase: ChangeReminderDateUseCaseImpl): ChangeReminderDateUseCase

    @Binds
    fun bindObserveCustomPeriodUseCase(useCase: ObserveCustomPeriodUseCaseImpl): ObserveCustomPeriodUseCase

    @Binds
    fun bindChangeReminderPeriodUseCase(useCase: ChangeReminderPeriodUseCaseImpl): ChangeReminderPeriodUseCase

    @Binds
    fun bindCancelAlarmUseCase(useCase: CancelInitialAlarmUseCaseImpl): CancelInitialAlarmUseCase

    @Binds
    fun bindFixInitialReminderDateUseCase(useCase: SaveInitialReminderDateUseCaseImpl): SaveInitialReminderDateUseCase

    @Binds
    fun bindSetResultAlarmUseCase(useCase: SetResultAlarmUseCaseImpl): SetResultAlarmUseCase

    @Binds
    fun bindRestoreInitialReminderUseCase(useCase: RestoreInitialReminderUseCaseImpl): RestoreInitialReminderUseCase
}