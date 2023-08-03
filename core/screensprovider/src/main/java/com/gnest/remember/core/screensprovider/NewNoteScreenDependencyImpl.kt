package com.gnest.remember.core.screensprovider

import com.gnest.remember.core.navigation.Screen
import com.gnest.remember.feature.interestingidea.navigation.ScreensProvider
import com.gnest.remember.feature.newnote.navigation.ScreenDependency
import javax.inject.Inject

internal class NewNoteScreenDependencyImpl @Inject constructor(private val screensProvider: ScreensProvider) : ScreenDependency {

    override fun getInterestingIdeaScreen(): Screen = screensProvider.provideInterestingIdeaScreen()
}