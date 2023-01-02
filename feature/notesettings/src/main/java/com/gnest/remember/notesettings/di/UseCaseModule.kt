package com.gnest.remember.notesettings.di

import com.gnest.remember.notesettings.domain.ObserveNoteColorUseCase
import com.gnest.remember.notesettings.domain.ObserveNoteColorUseCaseImpl
import com.gnest.remember.notesettings.domain.SaveNoteColorUseCase
import com.gnest.remember.notesettings.domain.SaveNoteColorUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface UseCaseModule {

    @Binds
    fun bindObserveNoteColorUseCase(useCase: ObserveNoteColorUseCaseImpl): ObserveNoteColorUseCase

    @Binds
    fun bindSaveNoteColorUseCase(useCase: SaveNoteColorUseCaseImpl): SaveNoteColorUseCase
}