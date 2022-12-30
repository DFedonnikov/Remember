package com.gnest.remember.feature.newnote.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import com.gnest.remember.navigation.extensions.modal
import com.gnest.remember.feature.newnote.NewNoteRoute
import com.gnest.remember.navigation.NewNoteScreen
import com.gnest.remember.navigation.extensions.pushExitTransition
import com.gnest.remember.navigation.extensions.pushPopEnterTransition

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.newNoteScreen() {
    modal(route = NewNoteScreen.route,
        exitTransition = { pushExitTransition },
        popEnterTransition = { pushPopEnterTransition }) {
        NewNoteRoute()
    }
}