package com.gnest.remember.feature.settings.navigation

import com.gnest.remember.core.navigation.Screen
import javax.inject.Inject

interface SettingsScreensProvider {

    fun provideSettingsScreen(): Screen
}

internal class SettingsScreensProviderImpl @Inject constructor() : SettingsScreensProvider {

    override fun provideSettingsScreen(): Screen = SettingsScreen
}