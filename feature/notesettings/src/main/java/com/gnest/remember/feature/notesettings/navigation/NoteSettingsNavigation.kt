package com.gnest.remember.feature.notesettings.navigation

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import com.gnest.remember.core.navigation.extensions.bottomSheet
import com.gnest.remember.feature.notesettings.NoteSettingsRoute

fun NavGraphBuilder.noteSettingsBottomSheet() {
    navigation(
        route = NoteSettingsScreen.graphPattern,
        startDestination = NoteSettingsScreen.routePattern,
        arguments = NoteSettingsScreen.args,
    ) {
        val modifier = Modifier.navigationBarsPadding()
        bottomSheet(
            route = NoteSettingsScreen.routePattern,
            arguments = NoteSettingsScreen.args,
        ) {
            NoteSettingsRoute(modifier = modifier)
        }
    }

}

internal val SavedStateHandle.noteId: Long
    get() = requireNotNull(get<Long>(NoteSettingsScreen.noteIdArg))