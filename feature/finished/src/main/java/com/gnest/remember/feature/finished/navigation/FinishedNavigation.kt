package com.gnest.remember.feature.finished.navigation

import androidx.navigation.NavGraphBuilder
import com.gnest.remember.feature.finished.FinishedRoute
import com.gnest.remember.core.navigation.extensions.push

fun NavGraphBuilder.finishedScreen() {
    push(route = FinishedScreen.route) {
        FinishedRoute()
    }
}