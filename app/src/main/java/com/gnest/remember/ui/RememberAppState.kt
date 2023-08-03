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
import com.gnest.remember.core.navigation.Navigator
import com.gnest.remember.core.screensprovider.AppModuleScreenDependency
import com.gnest.remember.navigation.TopLevelDestination
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun rememberAppState(
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(),
    navController: NavHostController = rememberNavController(bottomSheetNavigator),
    screenDependency: AppModuleScreenDependency,
    navigator: Navigator
) =
    remember(navController) {
        RememberAppState(
            navController,
            bottomSheetNavigator,
            screenDependency,
            navigator
        )
    }

@OptIn(ExperimentalMaterialNavigationApi::class)
@Stable
class RememberAppState(
    val navController: NavHostController,
    val bottomSheetNavigator: BottomSheetNavigator,
    private val screenDependency: AppModuleScreenDependency,
    private val navigator: Navigator
) {

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val isBottomBarVisible @Composable get() = currentTopLevelDestination != null

    private val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            screenDependency.getHomeScreen().route -> TopLevelDestination.HOME
            screenDependency.getFinishedScreen().route -> TopLevelDestination.FINISHED
            screenDependency.getSearchScreen().route -> TopLevelDestination.SEARCH
            screenDependency.getSettingsScreen().route -> TopLevelDestination.SETTINGS
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
            TopLevelDestination.HOME -> navigator.navigateTo(screenDependency.getHomeScreen(), topLevelNavOptions)
            TopLevelDestination.FINISHED -> navigator.navigateTo(screenDependency.getFinishedScreen(), topLevelNavOptions)
            TopLevelDestination.SEARCH -> navigator.navigateTo(screenDependency.getSearchScreen(), topLevelNavOptions)
            TopLevelDestination.SETTINGS -> navigator.navigateTo(screenDependency.getSettingsScreen(), topLevelNavOptions)
        }
    }
}