package com.gnest.remember.notesettings.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import com.gnest.remember.navigation.NoteSettingsScreen
import com.gnest.remember.navigation.extensions.bottomSheet
import com.gnest.remember.notesettings.NoteSettingsRoute

fun NavGraphBuilder.noteSettingsBottomSheet() {
    bottomSheet(
        route = NoteSettingsScreen.routePattern,
        arguments = NoteSettingsScreen.args,
    ) {
        NoteSettingsRoute()
    }
}

internal val SavedStateHandle.noteId: Long
    get() = requireNotNull(get<Long>(NoteSettingsScreen.noteIdArg))