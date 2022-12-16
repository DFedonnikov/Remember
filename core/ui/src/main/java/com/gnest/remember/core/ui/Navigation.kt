package com.gnest.remember.core.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.push(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = { slideIntoContainer(AnimatedContentScope.SlideDirection.Left) },
        exitTransition = { slideOutOfContainer(AnimatedContentScope.SlideDirection.Right) },
        content = content
    )
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.modal(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentScope.SlideDirection.Up,
                initialOffset = { Int.MAX_VALUE })
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentScope.SlideDirection.Down,
                targetOffset = { Int.MAX_VALUE })
        },
        content = content
    )
}