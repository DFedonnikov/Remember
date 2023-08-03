package com.gnest.remember.feature.newnote.di

import com.gnest.remember.feature.newnote.navigation.NewNoteScreensProvider
import com.gnest.remember.feature.newnote.navigation.NewNoteScreensProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface NewNoteScreensModule {

    @Binds
    fun bindScreensProvider(provider: NewNoteScreensProviderImpl): NewNoteScreensProvider
}