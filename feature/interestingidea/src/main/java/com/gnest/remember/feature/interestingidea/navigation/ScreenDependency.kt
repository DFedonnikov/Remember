package com.gnest.remember.feature.interestingidea.navigation

import com.gnest.remember.core.navigation.Screen

interface ScreenDependency {

    fun getNoteSettingsScreen(id: Long): Screen
}