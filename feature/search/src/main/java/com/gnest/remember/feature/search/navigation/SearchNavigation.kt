package com.gnest.remember.feature.search.navigation

import androidx.navigation.NavGraphBuilder
import com.gnest.remember.feature.search.SearchRoute
import com.gnest.remember.core.navigation.extensions.push

fun NavGraphBuilder.searchScreen() {
    push(route = SearchScreen.route) {
        SearchRoute()
    }
}