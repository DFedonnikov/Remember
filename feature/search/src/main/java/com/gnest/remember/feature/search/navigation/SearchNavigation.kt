package com.gnest.remember.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.gnest.remember.feature.search.SearchRoute

const val searchRoute = "searchRoute"

fun NavController.navigateToSearch(navOptions: NavOptions? = null) {
    this.navigate(searchRoute, navOptions)
}

fun NavGraphBuilder.searchScreen() {
    composable(route = searchRoute) {
        SearchRoute()
    }
}