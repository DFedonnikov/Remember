package com.gnest.remember.feature.home.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.gnest.remember.core.ui.push
import com.gnest.remember.feature.home.HomeRoute

const val homeRoute = "homeRoute"

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(homeRoute, navOptions)
}

fun NavGraphBuilder.homeScreen(
    interestingIdeas: () -> (@Composable () -> Unit)
) {
    push(route = homeRoute) {
        HomeRoute(interestingNotes = interestingIdeas())
    }
}