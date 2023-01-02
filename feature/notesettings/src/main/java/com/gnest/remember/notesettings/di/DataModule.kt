package com.gnest.remember.notesettings.di

import com.gnest.remember.notesettings.data.NoteSettingsRepository
import com.gnest.remember.notesettings.data.NoteSettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindNoteSettingsRepository(repository: NoteSettingsRepositoryImpl): NoteSettingsRepository
}