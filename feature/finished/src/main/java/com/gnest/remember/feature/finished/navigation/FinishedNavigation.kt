package com.gnest.remember.feature.finished.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.gnest.remember.feature.finished.FinishedRoute

const val finishedRoute = "finishedRoute"

fun NavController.navigateToFinished(navOptions: NavOptions? = null) {
    this.navigate(finishedRoute, navOptions)
}

fun NavGraphBuilder.finishedScreen() {
    composable(route = finishedRoute) {
        FinishedRoute()
    }
}