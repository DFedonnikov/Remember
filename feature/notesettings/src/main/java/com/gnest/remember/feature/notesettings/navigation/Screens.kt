package com.gnest.remember.feature.notesettings.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.gnest.remember.core.navigation.Screen

class NoteSettingsScreen(private val id: Long) : Screen {

    override val route get() = "$noteSettingsRoute/$id"
    override val popBackRoute: String = "$noteSettingsRoute/{$noteIdArg}"

    companion object {

        private const val noteSettingsRoute = "noteSettingsRoute"
        const val noteIdArg = "NoteId"
        const val graphPattern = "noteSettingsGraphRoute"
        const val routePattern = "$noteSettingsRoute/{$noteIdArg}"
        val args = listOf(navArgument(noteIdArg) { type = NavType.LongType })

    }
}