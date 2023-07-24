package com.gnest.remember.feature.notesettings.di

import com.gnest.remember.feature.notesettings.domain.ObserveNoteColorUseCase
import com.gnest.remember.feature.notesettings.domain.ObserveNoteColorUseCaseImpl
import com.gnest.remember.feature.notesettings.domain.ObserveNoteReminderDateUseCase
import com.gnest.remember.feature.notesettings.domain.ObserveNoteReminderDateUseCaseImpl
import com.gnest.remember.feature.notesettings.domain.SaveNoteColorUseCase
import com.gnest.remember.feature.notesettings.domain.SaveNoteColorUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface UseCaseModule {

    @Binds
    fun bindObserveNoteUseCase(useCase: ObserveNoteReminderDateUseCaseImpl): ObserveNoteReminderDateUseCase


    @Binds
    fun bindObserveNoteColorUseCase(useCase: ObserveNoteColorUseCaseImpl): ObserveNoteColorUseCase

    @Binds
    fun bindSaveNoteColorUseCase(useCase: SaveNoteColorUseCaseImpl): SaveNoteColorUseCase
}