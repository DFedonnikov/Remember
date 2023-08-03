package com.gnest.remember.feature.permissionprovider.navigation

import com.gnest.remember.core.navigation.Screen
import com.gnest.remember.core.permission.Permission
import javax.inject.Inject

interface PermissionScreensProvider {

    fun providePermissionScreen(permission: Permission): Screen
}

internal class PermissionScreensProviderImpl @Inject constructor() : PermissionScreensProvider {

    override fun providePermissionScreen(permission: Permission): Screen = PermissionScreen(permission)

}