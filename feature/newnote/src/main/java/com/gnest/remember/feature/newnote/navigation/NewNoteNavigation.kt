package com.gnest.remember.feature.newnote.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.gnest.remember.core.ui.modal
import com.gnest.remember.feature.newnote.NewNoteRoute

const val newNoteRoute = "newNoteRoute"

fun NavController.navigateToNewNote(navOptions: NavOptions? = null) {
    this.navigate(newNoteRoute, navOptions)
}

fun NavGraphBuilder.newNoteScreen(navigateToInterestingIdea: () -> Unit, onBackClick: () -> Unit) {
    modal(route = newNoteRoute) {
        NewNoteRoute(
            navigateToInterestingIdea = navigateToInterestingIdea,
            onBackClick = onBackClick
        )
    }
}