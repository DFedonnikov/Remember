package com.gnest.remember.core.screensprovider

import com.gnest.remember.core.navigation.Screen
import com.gnest.remember.feature.interestingidea.navigation.ScreenDependency
import com.gnest.remember.feature.notesettings.navigation.ScreensProvider
import javax.inject.Inject

internal class InterestingIdeaScreenDependencyImpl @Inject constructor(private val screensProvider: ScreensProvider) : ScreenDependency {

    override fun getNoteSettingsScreen(id: Long): Screen = screensProvider.provideNoteSettingsScreen(id)

}