package com.gnest.remember.feature.home.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import com.gnest.remember.feature.home.HomeRoute
import com.gnest.remember.navigation.HomeScreen
import com.gnest.remember.navigation.NewNoteScreen
import com.gnest.remember.navigation.extensions.modalExitTransition
import com.gnest.remember.navigation.extensions.modalPopEnterTransition
import com.gnest.remember.navigation.extensions.push
import com.gnest.remember.navigation.extensions.pushExitTransition
import com.gnest.remember.navigation.extensions.pushPopEnterTransition

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.homeScreen(
    interestingIdeas: () -> (@Composable () -> Unit)
) {
    push(route = HomeScreen.route,
        exitTransition = {
            when (targetState.destination.route) {
                NewNoteScreen.route -> modalExitTransition
                else -> pushExitTransition
            }
        },
        popEnterTransition = {
            when (initialState.destination.route) {
                NewNoteScreen.route -> modalPopEnterTransition
                else -> pushPopEnterTransition
            }
        }) {
        HomeRoute(interestingNotes = interestingIdeas())
    }
}