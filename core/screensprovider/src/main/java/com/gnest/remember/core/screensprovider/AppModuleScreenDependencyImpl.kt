package com.gnest.remember.core.screensprovider

import com.gnest.remember.core.navigation.Screen
import com.gnest.remember.feature.finished.navigation.FinishedScreensProvider
import com.gnest.remember.feature.home.navigation.HomeScreensProvider
import com.gnest.remember.feature.newnote.navigation.NewNoteScreensProvider
import com.gnest.remember.feature.search.navigation.SearchScreensProvider
import com.gnest.remember.feature.settings.navigation.SettingsScreensProvider
import javax.inject.Inject

interface AppModuleScreenDependency {

    fun getHomeScreen(): Screen
    fun getFinishedScreen(): Screen
    fun getSearchScreen(): Screen
    fun getSettingsScreen(): Screen
    fun getNewNoteScreen(): Screen
}

class AppModuleScreenDependencyImpl @Inject constructor(
    private val homeScreenProvider: HomeScreensProvider,
    private val finishedScreenProvider: FinishedScreensProvider,
    private val searchScreensProvider: SearchScreensProvider,
    private val settingsScreensProvider: SettingsScreensProvider,
    private val newNoteScreensProvider: NewNoteScreensProvider
) : AppModuleScreenDependency {

    override fun getHomeScreen(): Screen = homeScreenProvider.provideHomeScreen()

    override fun getFinishedScreen(): Screen = finishedScreenProvider.provideFinishedScreen()

    override fun getSearchScreen(): Screen = searchScreensProvider.provideSearchScreen()

    override fun getSettingsScreen(): Screen = settingsScreensProvider.provideSettingsScreen()

    override fun getNewNoteScreen(): Screen = newNoteScreensProvider.provideNewNoteScreen()

}