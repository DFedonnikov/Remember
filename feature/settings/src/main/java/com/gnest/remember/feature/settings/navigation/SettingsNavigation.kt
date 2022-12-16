package com.gnest.remember.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.gnest.remember.core.ui.push
import com.gnest.remember.feature.settings.SettingsRoute

const val settingsRoute = "settingsRoute"

fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
    this.navigate(settingsRoute, navOptions)
}

fun NavGraphBuilder.settingsScreen() {
    push(route = settingsRoute) {
        SettingsRoute()
    }
}