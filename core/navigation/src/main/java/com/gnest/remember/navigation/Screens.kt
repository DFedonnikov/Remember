package com.gnest.remember.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface Screen {

    val route: String
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