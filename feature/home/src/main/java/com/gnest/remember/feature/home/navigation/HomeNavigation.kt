package com.gnest.remember.feature.home.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import com.gnest.remember.feature.home.HomeRoute
import com.gnest.remember.core.navigation.HomeScreen
import com.gnest.remember.core.navigation.NewNoteScreen
import com.gnest.remember.core.navigation.extensions.modalExitTransition
import com.gnest.remember.core.navigation.extensions.modalPopEnterTransition
import com.gnest.remember.core.navigation.extensions.push
import com.gnest.remember.core.navigation.extensions.pushExitTransition
import com.gnest.remember.core.navigation.extensions.pushPopEnterTransition

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