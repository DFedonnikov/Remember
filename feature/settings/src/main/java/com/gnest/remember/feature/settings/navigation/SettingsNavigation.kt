package com.gnest.remember.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import com.gnest.remember.feature.settings.SettingsRoute
import com.gnest.remember.core.navigation.SettingsScreen
import com.gnest.remember.core.navigation.extensions.push


fun NavGraphBuilder.settingsScreen() {
    push(route = SettingsScreen.route) {
        SettingsRoute()
    }
}