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
import com.gnest.remember.navigation.FinishedScreen
import com.gnest.remember.navigation.HomeScreen
import com.gnest.remember.navigation.Navigator
import com.gnest.remember.navigation.SearchScreen
import com.gnest.remember.navigation.SettingsScreen
import com.gnest.remember.navigation.TopLevelDestination
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun rememberAppState(
    navController: NavHostController = rememberAnimatedNavController(),
    navigator: Navigator
) =
    remember(navController) {
        RememberAppState(navController, navigator)
    }

@Stable
class RememberAppState(val navController: NavHostController, private val navigator: Navigator) {

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val isBottomBarVisible @Composable get() = currentTopLevelDestination != null

    private val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            HomeScreen.route -> TopLevelDestination.HOME
            FinishedScreen.route -> TopLevelDestination.FINISHED
            SearchScreen.route -> TopLevelDestination.SEARCH
            SettingsScreen.route -> TopLevelDestination.SETTINGS
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
            TopLevelDestination.HOME -> navigator.navigateTo(HomeScreen, topLevelNavOptions)
            TopLevelDestination.FINISHED -> navigator.navigateTo(FinishedScreen, topLevelNavOptions)
            TopLevelDestination.SEARCH -> navigator.navigateTo(SearchScreen, topLevelNavOptions)
            TopLevelDestination.SETTINGS -> navigator.navigateTo(SettingsScreen, topLevelNavOptions)
        }
    }
}