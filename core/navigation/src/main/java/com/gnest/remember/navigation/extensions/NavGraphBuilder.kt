package com.gnest.remember.navigation.extensions

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

private val animationSpec get() = tween<IntOffset>(500)

@OptIn(ExperimentalAnimationApi::class)
val AnimatedContentScope<*>.pushEnterTransition: EnterTransition
    get() = slideIntoContainer(
        AnimatedContentScope.SlideDirection.Left,
        animationSpec = animationSpec
    )

@OptIn(ExperimentalAnimationApi::class)
val AnimatedContentScope<*>.pushExitTransition: ExitTransition
    get() = slideOutOfContainer(
        AnimatedContentScope.SlideDirection.Left,
        animationSpec = animationSpec
    )


@OptIn(ExperimentalAnimationApi::class)
val AnimatedContentScope<*>.pushPopEnterTransition: EnterTransition
    get() = slideIntoContainer(
        AnimatedContentScope.SlideDirection.Right,
        animationSpec = animationSpec
    )


@OptIn(ExperimentalAnimationApi::class)
val AnimatedContentScope<*>.pushPopExitTransition: ExitTransition
    get() = slideOutOfContainer(
        AnimatedContentScope.SlideDirection.Right,
        animationSpec = animationSpec
    )

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.push(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = { pushEnterTransition },
    exitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = { pushExitTransition },
    popEnterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = { pushPopEnterTransition },
    popExitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = { pushPopExitTransition },
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

@OptIn(ExperimentalAnimationApi::class)
val AnimatedContentScope<*>.modalEnterTransition: EnterTransition
    get() = slideIntoContainer(
        AnimatedContentScope.SlideDirection.Up,
        animationSpec = animationSpec
    )

@OptIn(ExperimentalAnimationApi::class)
val AnimatedContentScope<*>.modalExitTransition: ExitTransition
    get() = slideOutOfContainer(
        AnimatedContentScope.SlideDirection.Up,
        animationSpec = animationSpec
    )

@OptIn(ExperimentalAnimationApi::class)
val AnimatedContentScope<*>.modalPopEnterTransition: EnterTransition
    get() = slideIntoContainer(
        AnimatedContentScope.SlideDirection.Down,
        animationSpec = animationSpec
    )

@OptIn(ExperimentalAnimationApi::class)
val AnimatedContentScope<*>.modalPopExitTransition: ExitTransition
    get() = slideOutOfContainer(
        AnimatedContentScope.SlideDirection.Down,
        animationSpec = animationSpec
    )


@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.modal(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = { modalEnterTransition },
    exitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = { modalExitTransition },
    popEnterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = { modalPopEnterTransition },
    popExitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = { modalPopExitTransition },
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