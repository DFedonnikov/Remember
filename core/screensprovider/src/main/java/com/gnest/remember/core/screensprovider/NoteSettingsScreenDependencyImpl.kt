package com.gnest.remember.core.screensprovider

import com.gnest.remember.core.navigation.Screen
import com.gnest.remember.feature.notesettings.navigation.ScreenDependency
import com.gnest.remember.feature.reminder.navigation.ScreensProvider
import javax.inject.Inject

class NoteSettingsScreenDependencyImpl @Inject constructor(private val screensProvider: ScreensProvider): ScreenDependency {

    override fun getReminderScreen(id: Long): Screen = screensProvider.provideReminderScreen(id)
}