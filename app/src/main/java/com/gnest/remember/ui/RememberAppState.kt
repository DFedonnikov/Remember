package com.gnest.remember.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.gnest.remember.feature.finished.navigation.navigateToFinished
import com.gnest.remember.feature.home.navigation.navigateToHome
import com.gnest.remember.feature.search.navigation.navigateToSearch
import com.gnest.remember.feature.settings.navigation.navigateToSettings
import com.gnest.remember.navigation.TopLevelDestination

@Composable
fun rememberAppState(navController: NavHostController = rememberNavController()): RememberAppState {
    return remember(navController) {
        RememberAppState(navController)
    }
}

@Stable
class RememberAppState(val navController: NavHostController) {

    val currentDestination: NavDestination?
        @Composable get() = navController
                .currentBackStackEntryAsState().value?.destination

//    val currentTopLevelDestination: TopLevelDestination?
//        @Composable get() = when (currentDestination?.route) {
//            "homeRoute" -> TopLevelDestination.HOME
//            "finishedRoute" -> TopLevelDestination.FINISHED
//            "searchRoute" -> TopLevelDestination.SEARCH
//            "settingsRoute" -> TopLevelDestination.SETTINGS
//            else -> null
//        }

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

            when (topLevelDestination) {
                TopLevelDestination.HOME -> navController.navigateToHome()
                TopLevelDestination.FINISHED -> navController.navigateToFinished()
                TopLevelDestination.SEARCH -> navController.navigateToSearch()
                TopLevelDestination.SETTINGS -> navController.navigateToSettings()
            }
        }
    }

    fun onBackClick() {
        navController.popBackStack()
    }
}