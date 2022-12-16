package com.gnest.remember.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import com.gnest.remember.feature.finished.navigation.finishedRoute
import com.gnest.remember.feature.finished.navigation.navigateToFinished
import com.gnest.remember.feature.home.navigation.homeRoute
import com.gnest.remember.feature.home.navigation.navigateToHome
import com.gnest.remember.feature.search.navigation.navigateToSearch
import com.gnest.remember.feature.search.navigation.searchRoute
import com.gnest.remember.feature.settings.navigation.navigateToSettings
import com.gnest.remember.feature.settings.navigation.settingsRoute
import com.gnest.remember.navigation.TopLevelDestination
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun rememberAppState(navController: NavHostController = rememberAnimatedNavController()): RememberAppState {
    return remember(navController) {
        RememberAppState(navController)
    }
}

@Stable
class RememberAppState(val navController: NavHostController) {

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val isBottomBarVisible @Composable get() = currentTopLevelDestination != null

    private val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            homeRoute -> TopLevelDestination.HOME
            finishedRoute -> TopLevelDestination.FINISHED
            searchRoute -> TopLevelDestination.SEARCH
            settingsRoute -> TopLevelDestination.SETTINGS
            else -> null
        }

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().asList()

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }

        when (topLevelDestination) {
            TopLevelDestination.HOME -> navController.navigateToHome(topLevelNavOptions)
            TopLevelDestination.FINISHED -> navController.navigateToFinished(topLevelNavOptions)
            TopLevelDestination.SEARCH -> navController.navigateToSearch(topLevelNavOptions)
            TopLevelDestination.SETTINGS -> navController.navigateToSettings(topLevelNavOptions)
        }
    }

    fun onBackClick() {
        navController.popBackStack()
    }
}