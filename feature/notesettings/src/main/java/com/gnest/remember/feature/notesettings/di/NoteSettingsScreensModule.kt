package com.gnest.remember.feature.notesettings.di

import com.gnest.remember.feature.notesettings.navigation.ScreensProvider
import com.gnest.remember.feature.notesettings.navigation.ScreensProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface NoteSettingsScreensModule {

    @Binds
    fun bindScreensProvider(provider: ScreensProviderImpl): ScreensProvider
}