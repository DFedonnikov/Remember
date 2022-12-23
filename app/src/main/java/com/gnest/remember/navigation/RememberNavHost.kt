package com.gnest.remember.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.gnest.remember.feature.finished.navigation.finishedScreen
import com.gnest.remember.feature.home.navigation.homeRoute
import com.gnest.remember.feature.home.navigation.homeScreen
import com.gnest.remember.feature.newnote.navigation.newNoteScreen
import com.gnest.remember.feature.settings.navigation.settingsScreen
import com.gnest.remember.feature.search.navigation.searchScreen
import com.gnest.remember.interestingidea.navigation.interestingIdeaScreen
import com.gnest.remember.interestingidea.navigation.navigateToInterestingIdea
import com.google.accompanist.navigation.animation.AnimatedNavHost

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RememberNavHost(
        navController: NavHostController,
        onBackClick: () -> Unit,
        modifier: Modifier = Modifier,
        startDestination: String = homeRoute
) {
    AnimatedNavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier,
    ) {
        homeScreen()
        finishedScreen()
        searchScreen()
        settingsScreen()
        newNoteScreen(navigateToInterestingIdea = {
            navController.navigateToInterestingIdea()
        }, onBackClick)
        interestingIdeaScreen(onBackClick)
    }
}