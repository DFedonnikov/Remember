package com.gnest.remember.core.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface Screen {

    val route: String
    val popBackRoute: String get() = route
}

object HomeScreen : Screen {

    override val route get() = "homeRoute"

}

object FinishedScreen : Screen {

    override val route: String get() = "finishedRoute"
}

object SearchScreen : Screen {

    override val route: String get() = "searchRoute"
}

object SettingsScreen : Screen {

    override val route: String get() = "settingsRoute"
}

object NewNoteScreen : Screen {

    override val route: String get() = "newNoteRoute"
}

class InterestingIdeaScreen(private val id: Long = -1L) : Screen {

    override val route get() = "$interestingIdeaRoute/$id"

    companion object {

        private const val interestingIdeaRoute = "interestingIdeaRoute"
        const val ideaIdArg = "InterestingIdeaId"
        const val routePattern = "$interestingIdeaRoute/{$ideaIdArg}"
        val args = listOf(navArgument(ideaIdArg) { type = NavType.LongType })

    }
}

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

class ReminderScreen(private val id: Long) : Screen {

    override val route get() = "$reminderRoute/$id"
    override val popBackRoute: String = "$reminderRoute/{$noteIdArg}"

    companion object {

        private const val reminderRoute = "reminderRoute"
        const val noteIdArg = "NoteId"
        const val graphPattern = "reminderGraphRoute"
        const val routePattern = "$reminderRoute/{$noteIdArg}"
        val args = listOf(navArgument(noteIdArg) { type = NavType.LongType })

    }
}