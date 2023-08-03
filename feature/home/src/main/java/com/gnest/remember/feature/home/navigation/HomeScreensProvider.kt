package com.gnest.remember.feature.home.navigation

import com.gnest.remember.core.navigation.Screen
import javax.inject.Inject

interface HomeScreensProvider {

    fun provideHomeScreen(): Screen
}

internal class HomeScreensProviderImpl @Inject constructor(): HomeScreensProvider {

    override fun provideHomeScreen(): Screen = HomeScreen
}