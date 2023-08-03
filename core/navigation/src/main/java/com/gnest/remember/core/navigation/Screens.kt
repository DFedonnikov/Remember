package com.gnest.remember.core.navigation

interface Screen {

    val route: String
    val popBackRoute: String get() = route
}