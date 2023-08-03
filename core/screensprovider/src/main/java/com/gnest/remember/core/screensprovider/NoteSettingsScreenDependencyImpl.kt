package com.gnest.remember.core.screensprovider

import com.gnest.remember.core.navigation.Screen
import com.gnest.remember.core.permission.Permission
import com.gnest.remember.feature.notesettings.navigation.ScreenDependency
import com.gnest.remember.feature.permissionprovider.navigation.PermissionScreensProvider
import com.gnest.remember.feature.reminder.navigation.ReminderScreensProvider
import javax.inject.Inject

class NoteSettingsScreenDependencyImpl @Inject constructor(private val reminderScreensProvider: ReminderScreensProvider,
                                                           private val permissionScreensProvider: PermissionScreensProvider): ScreenDependency {

    override fun getReminderScreen(id: Long): Screen = reminderScreensProvider.provideReminderScreen(id)

    override fun getPermissionScreen(permission: Permission): Screen = permissionScreensProvider.providePermissionScreen(permission)
}