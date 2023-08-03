package com.gnest.remember.feature.notesettings.navigation

import com.gnest.remember.core.navigation.Screen
import com.gnest.remember.core.permission.Permission

interface ScreenDependency {

    fun getReminderScreen(id: Long): Screen
    fun getPermissionScreen(permission: Permission): Screen
}