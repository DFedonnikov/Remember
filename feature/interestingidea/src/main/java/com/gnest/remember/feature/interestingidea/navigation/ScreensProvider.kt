package com.gnest.remember.feature.interestingidea.navigation

import com.gnest.remember.core.navigation.Screen
import javax.inject.Inject

interface ScreensProvider {

    fun provideInterestingIdeaScreen(id: Long? = null): Screen
}

internal class ScreensProviderImpl @Inject constructor() : ScreensProvider {

    override fun provideInterestingIdeaScreen(id: Long?): Screen = InterestingIdeaScreen(id)
}