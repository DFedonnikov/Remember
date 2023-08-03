package com.gnest.remember.core.screensprovider

import com.gnest.remember.core.navigation.Screen
import com.gnest.remember.feature.notesettings.navigation.ScreensProvider
import com.gnest.remember.feature.reminder.navigation.ScreenDependency
import javax.inject.Inject

class ReminderScreenDependencyImpl @Inject constructor(private val screensProvider: ScreensProvider) : ScreenDependency {

    override fun getNoteSettingsScreen(id: Long): Screen = screensProvider.provideNoteSettingsScreen(id)
}