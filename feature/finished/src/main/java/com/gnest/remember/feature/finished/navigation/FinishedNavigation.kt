package com.gnest.remember.feature.finished.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import com.gnest.remember.feature.finished.FinishedRoute
import com.gnest.remember.navigation.FinishedScreen
import com.gnest.remember.navigation.extensions.push

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.finishedScreen() {
    push(route = FinishedScreen.route) {
        FinishedRoute()
    }
}