package com.gnest.remember.feature.settings.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import com.gnest.remember.feature.settings.SettingsRoute
import com.gnest.remember.navigation.SettingsScreen
import com.gnest.remember.navigation.extensions.push


@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.settingsScreen() {
    push(route = SettingsScreen.route) {
        SettingsRoute()
    }
}