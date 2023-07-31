package com.gnest.remember.feature.reminder.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.gnest.remember.core.navigation.ReminderScreen
import com.gnest.remember.core.navigation.Screen

internal class ReminderDateScreen(id: Long) : Screen {

    override val route: String = "$reminderDateRoute/$id"

    internal companion object {

        private const val reminderDateRoute = "reminderDateRoute"
        internal const val routePattern = "$reminderDateRoute/{${ReminderScreen.noteIdArg}}"
        internal val args = listOf(navArgument(ReminderScreen.noteIdArg) { type = NavType.LongType })

    }
}

internal class ReminderTimeScreen(id: Long) : Screen {

    override val route: String = "$reminderTimeRoute/$id"

    internal companion object {

        private const val reminderTimeRoute = "reminderTimeRoute"
        internal const val routePattern = "$reminderTimeRoute/{${ReminderScreen.noteIdArg}}"
        internal val args = listOf(navArgument(ReminderScreen.noteIdArg) { type = NavType.LongType })

    }
}

internal class ReminderRepeatScreen(id: Long) : Screen {

    override val route: String = "$reminderRepeatRoute/$id"

    internal companion object {

        private const val reminderRepeatRoute = "reminderRepeatRoute"
        internal const val routePattern = "$reminderRepeatRoute/{${ReminderScreen.noteIdArg}}"
        internal val args = listOf(navArgument(ReminderScreen.noteIdArg) { type = NavType.LongType })

    }
}

internal class ReminderRepeatIntervalScreen(id: Long) : Screen {

    override val route: String = "$reminderRepeatIntervalRoute/$id"

    internal companion object {

        private const val reminderRepeatIntervalRoute = "reminderRepeatIntervalRoute"
        internal const val routePattern = "$reminderRepeatIntervalRoute/{${ReminderScreen.noteIdArg}}"
        internal val args = listOf(navArgument(ReminderScreen.noteIdArg) { type = NavType.LongType })

    }
}

internal class ReminderApproveDialog(id: Long, isReturnToNoteSettings: Boolean = false) : Screen {

    override val route: String = "$reminderApproveIntervalRoute/$id/$isReturnToNoteSettings"

    internal companion object {

        private const val reminderApproveIntervalRoute = "reminderApproveIntervalRoute"
        internal const val isReturnToNoteSettingsArg = "isReturnToNoteSettings"
        internal const val routePattern = "$reminderApproveIntervalRoute/{${ReminderScreen.noteIdArg}}/{$isReturnToNoteSettingsArg}"
        internal val args = listOf(
            navArgument(ReminderScreen.noteIdArg) { type = NavType.LongType },
            navArgument(isReturnToNoteSettingsArg) { type = NavType.BoolType }
        )

    }
}