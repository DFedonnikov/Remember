package com.gnest.remember.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.gnest.remember.feature.finished.navigation.finishedScreen
import com.gnest.remember.feature.home.navigation.homeRoute
import com.gnest.remember.feature.home.navigation.homeScreen
import com.gnest.remember.feature.settings.navigation.settingsScreen
import com.gnest.remember.feature.search.navigation.searchScreen

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 */
@Composable
fun RememberNavHost(
        navController: NavHostController,
        onBackClick: () -> Unit,
        modifier: Modifier = Modifier,
        startDestination: String = homeRoute
) {
    NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier,
    ) {
        homeScreen()
        finishedScreen()
        searchScreen()
        settingsScreen()
    }
}