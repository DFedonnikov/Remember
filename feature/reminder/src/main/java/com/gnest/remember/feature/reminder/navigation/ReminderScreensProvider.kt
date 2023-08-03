package com.gnest.remember.feature.reminder.navigation

import com.gnest.remember.core.navigation.Screen
import javax.inject.Inject

interface ReminderScreensProvider {

    fun provideReminderScreen(id: Long): Screen
}

internal class ReminderScreensProviderImpl @Inject constructor() : ReminderScreensProvider {

    override fun provideReminderScreen(id: Long): Screen = ReminderScreen(id)
}