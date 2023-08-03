package com.gnest.remember.feature.reminder.navigation

import com.gnest.remember.core.navigation.Screen

interface ScreenDependency {

    fun getNoteSettingsScreen(id: Long): Screen
}