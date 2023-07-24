package com.gnest.remember.core.navigation.extensions

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet as materialBottomSheet

private val animationSpec get() = tween<IntOffset>(500)

val AnimatedContentTransitionScope<NavBackStackEntry>.pushEnterTransition: EnterTransition
    get() = slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = animationSpec
    )

val AnimatedContentTransitionScope<NavBackStackEntry>.pushExitTransition: ExitTransition
    get() = slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = animationSpec
    )


val AnimatedContentTransitionScope<NavBackStackEntry>.pushPopEnterTransition: EnterTransition
    get() = slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = animationSpec
    )


val AnimatedContentTransitionScope<NavBackStackEntry>.pushPopExitTransition: ExitTransition
    get() = slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = animationSpec
    )

fun NavGraphBuilder.push(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = { pushEnterTransition },
    exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = { pushExitTransition },
    popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = { pushPopEnterTransition },
    popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = { pushPopExitTransition },
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        content = content
    )
}

val AnimatedContentTransitionScope<NavBackStackEntry>.modalEnterTransition: EnterTransition
    get() = slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Up,
        animationSpec = animationSpec
    )

val AnimatedContentTransitionScope<NavBackStackEntry>.modalExitTransition: ExitTransition
    get() = slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Up,
        animationSpec = animationSpec
    )

val AnimatedContentTransitionScope<NavBackStackEntry>.modalPopEnterTransition: EnterTransition
    get() = slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Down,
        animationSpec = animationSpec
    )

val AnimatedContentTransitionScope<NavBackStackEntry>.modalPopExitTransition: ExitTransition
    get() = slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Down,
        animationSpec = animationSpec
    )


fun NavGraphBuilder.modal(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = { modalEnterTransition },
    exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = { modalExitTransition },
    popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = { modalPopEnterTransition },
    popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = { modalPopExitTransition },
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        content = content
    )
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.bottomSheet(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable ColumnScope.(backstackEntry: NavBackStackEntry) -> Unit
) {
    materialBottomSheet(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        content = content
    )
}