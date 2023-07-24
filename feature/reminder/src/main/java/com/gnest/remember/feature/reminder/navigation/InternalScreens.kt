package com.gnest.remember.feature.reminder.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.gnest.remember.core.navigation.NoteSettingsScreen
import com.gnest.remember.core.navigation.Screen

internal class ReminderDateScreen(id: Long) : Screen {

    override val route: String = "$reminderDateRoute/$id"

    internal companion object {

        private const val reminderDateRoute = "reminderDateRoute"
        const val routePattern = "$reminderDateRoute/{${NoteSettingsScreen.noteIdArg}}"
        val args = listOf(navArgument(NoteSettingsScreen.noteIdArg) { type = NavType.LongType })

    }
}

internal class ReminderTimeScreen(id: Long) : Screen {

    override val route: String = "$reminderTimeRoute/$id"

    internal companion object {

        private const val reminderTimeRoute = "reminderTimeRoute"
        const val routePattern = "$reminderTimeRoute/{${NoteSettingsScreen.noteIdArg}}"
        val args = listOf(navArgument(NoteSettingsScreen.noteIdArg) { type = NavType.LongType })

    }
}

internal class ReminderRepeatScreen(id: Long) : Screen {

    override val route: String = "$reminderRepeatRoute/$id"

    internal companion object {

        private const val reminderRepeatRoute = "reminderRepeatRoute"
        const val routePattern = "$reminderRepeatRoute/{${NoteSettingsScreen.noteIdArg}}"
        val args = listOf(navArgument(NoteSettingsScreen.noteIdArg) { type = NavType.LongType })

    }
}

internal class ReminderRepeatIntervalScreen(id: Long) : Screen {

    override val route: String = "$reminderRepeatIntervalRoute/$id"

    internal companion object {

        private const val reminderRepeatIntervalRoute = "reminderRepeatIntervalRoute"
        const val routePattern = "$reminderRepeatIntervalRoute/{${NoteSettingsScreen.noteIdArg}}"
        val args = listOf(navArgument(NoteSettingsScreen.noteIdArg) { type = NavType.LongType })

    }
}