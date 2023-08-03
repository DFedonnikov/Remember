package com.gnest.remember.feature.finished.navigation

import com.gnest.remember.core.navigation.Screen
import javax.inject.Inject

interface FinishedScreensProvider {

    fun provideFinishedScreen(): Screen
}

internal class FinishedScreensProviderImpl @Inject constructor() : FinishedScreensProvider {

    override fun provideFinishedScreen(): Screen = FinishedScreen
}