package com.gnest.remember.feature.settings.di

import com.gnest.remember.feature.settings.navigation.SettingsScreensProvider
import com.gnest.remember.feature.settings.navigation.SettingsScreensProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface SettingsScreensModule {

    @Binds
    fun bindSettingsScreensProvider(provider: SettingsScreensProviderImpl): SettingsScreensProvider
}