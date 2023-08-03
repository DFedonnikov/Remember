package com.gnest.remember.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.gnest.remember.core.navigation.Screen
import com.gnest.remember.core.navigation.extensions.modalExitTransition
import com.gnest.remember.core.navigation.extensions.modalPopEnterTransition
import com.gnest.remember.core.navigation.extensions.pushExitTransition
import com.gnest.remember.core.navigation.extensions.pushPopEnterTransition
import com.gnest.remember.feature.finished.navigation.finishedScreen
import com.gnest.remember.feature.home.navigation.homeScreen
import com.gnest.remember.feature.search.navigation.searchScreen
import com.gnest.remember.feature.settings.navigation.settingsScreen
import com.gnest.remember.feature.interestingidea.navigation.interestingIdeaScreen
import com.gnest.remember.feature.interestingidea.navigation.interestingIdeasRowList
import com.gnest.remember.feature.newnote.navigation.newNoteScreen
import com.gnest.remember.feature.notesettings.navigation.noteSettingsBottomSheet
import com.gnest.remember.feature.reminder.navigation.reminderBottomSheet

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 */
@Composable
fun RememberNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: Screen,
    newNoteScreen: Screen
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier,
    ) {
        homeScreen(
            exitTransition = {
                when (targetState.destination.route) {
                    newNoteScreen.route -> modalExitTransition
                    else -> pushExitTransition
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    newNoteScreen.route -> modalPopEnterTransition
                    else -> pushPopEnterTransition
                }
            },
            interestingIdeas = { interestingIdeasRowList() })
        finishedScreen()
        searchScreen()
        settingsScreen()
        newNoteScreen()
        interestingIdeaScreen()
        noteSettingsBottomSheet()
        reminderBottomSheet()
    }
}