package com.gnest.remember.feature.notesettings.di

import com.gnest.remember.feature.notesettings.data.NoteSettingsRepository
import com.gnest.remember.feature.notesettings.data.NoteSettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {

    @Binds
    fun bindNoteSettingsRepository(repository: NoteSettingsRepositoryImpl): NoteSettingsRepository
}