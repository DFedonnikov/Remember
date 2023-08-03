package com.gnest.remember.feature.home.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.gnest.remember.feature.home.HomeRoute
import com.gnest.remember.core.navigation.extensions.push

fun NavGraphBuilder.homeScreen(
    exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?,
    popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?,
    interestingIdeas: () -> (@Composable () -> Unit)
) {

    push(route = HomeScreen.route,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition) {
        HomeRoute(interestingNotes = interestingIdeas())
    }
}