package com.gnest.remember.feature.newnote.navigation

import com.gnest.remember.core.navigation.Screen
import javax.inject.Inject

interface NewNoteScreensProvider {

    fun provideNewNoteScreen(): Screen
}

internal class NewNoteScreensProviderImpl @Inject constructor() : NewNoteScreensProvider {

    override fun provideNewNoteScreen(): Screen = NewNoteScreen
}