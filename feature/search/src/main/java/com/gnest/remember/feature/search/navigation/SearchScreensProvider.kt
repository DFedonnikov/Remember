package com.gnest.remember.feature.search.navigation

import com.gnest.remember.core.navigation.Screen
import javax.inject.Inject

interface SearchScreensProvider {

    fun provideSearchScreen(): Screen
}

internal class SearchScreensProviderImpl @Inject constructor() : SearchScreensProvider {

    override fun provideSearchScreen(): Screen = SearchScreen
}