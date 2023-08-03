package com.gnest.remember.feature.permissionprovider.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import com.gnest.remember.feature.permissionprovider.PermissionDialog

fun NavGraphBuilder.permissionScreen() {
    dialog(
        route = PermissionScreen.routePattern,
        arguments = PermissionScreen.args
    ) {
        PermissionDialog()
    }
}