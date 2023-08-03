package com.gnest.remember.feature.newnote.navigation

import androidx.navigation.NavGraphBuilder
import com.gnest.remember.core.navigation.extensions.modal
import com.gnest.remember.feature.newnote.NewNoteRoute
import com.gnest.remember.core.navigation.extensions.pushExitTransition
import com.gnest.remember.core.navigation.extensions.pushPopEnterTransition

fun NavGraphBuilder.newNoteScreen() {
    modal(route = NewNoteScreen.route,
        exitTransition = { pushExitTransition },
        popEnterTransition = { pushPopEnterTransition }) {
        NewNoteRoute()
    }
}