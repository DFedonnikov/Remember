package com.gnest.remember.feature.reminder.navigation

import com.gnest.remember.core.navigation.Screen
import javax.inject.Inject

interface ScreensProvider {

    fun provideReminderScreen(id: Long): Screen
}

internal class ScreensProviderImpl @Inject constructor() : ScreensProvider {

    override fun provideReminderScreen(id: Long): Screen = ReminderScreen(id)
}