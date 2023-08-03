package com.gnest.remember.feature.notesettings.navigation

import com.gnest.remember.core.navigation.Screen
import javax.inject.Inject

interface ScreensProvider {

    fun provideNoteSettingsScreen(id: Long): Screen
}

internal class ScreensProviderImpl @Inject constructor() : ScreensProvider {

    override fun provideNoteSettingsScreen(id: Long): Screen = NoteSettingsScreen(id)
}