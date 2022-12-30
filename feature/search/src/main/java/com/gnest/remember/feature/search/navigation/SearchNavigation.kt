package com.gnest.remember.feature.search.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import com.gnest.remember.feature.search.SearchRoute
import com.gnest.remember.navigation.SearchScreen
import com.gnest.remember.navigation.extensions.push

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.searchScreen() {
    push(route = SearchScreen.route) {
        SearchRoute()
    }
}