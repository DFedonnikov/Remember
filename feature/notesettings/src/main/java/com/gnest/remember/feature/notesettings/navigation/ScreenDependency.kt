package com.gnest.remember.feature.notesettings.navigation

import com.gnest.remember.core.navigation.Screen

interface ScreenDependency {

    fun getReminderScreen(id: Long): Screen
}