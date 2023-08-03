package com.gnest.remember.feature.newnote.navigation

import com.gnest.remember.core.navigation.Screen

interface ScreenDependency {

    fun getInterestingIdeaScreen(): Screen
}