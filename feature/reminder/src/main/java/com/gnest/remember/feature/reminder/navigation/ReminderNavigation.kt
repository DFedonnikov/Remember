package com.gnest.remember.feature.reminder.navigation

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import com.gnest.remember.core.navigation.ReminderScreen
import com.gnest.remember.core.navigation.extensions.bottomSheet
import com.gnest.remember.feature.reminder.ReminderRoute
import com.gnest.remember.feature.reminder.approve.ApproveDialog
import com.gnest.remember.feature.reminder.date.ReminderDate
import com.gnest.remember.feature.reminder.repeat.ReminderRepeat
import com.gnest.remember.feature.reminder.customperiod.ReminderCustomPeriod
import com.gnest.remember.feature.reminder.time.ReminderTime

fun NavGraphBuilder.reminderBottomSheet() {
    navigation(
        route = ReminderScreen.graphPattern,
        startDestination = ReminderScreen.routePattern,
        arguments = ReminderScreen.args,
    ) {
        val modifier = Modifier.navigationBarsPadding()
        bottomSheet(
            route = ReminderScreen.routePattern,
            arguments = ReminderScreen.args,
        ) {
            ReminderRoute(modifier = modifier)
        }
        bottomSheet(
            route = ReminderDateScreen.routePattern,
            arguments = ReminderDateScreen.args
        ) {
            ReminderDate(modifier = modifier)
        }
        bottomSheet(
            route = ReminderTimeScreen.routePattern,
            arguments = ReminderTimeScreen.args
        ) {
            ReminderTime(modifier = modifier)
        }
        bottomSheet(
            route = ReminderRepeatScreen.routePattern,
            arguments = ReminderRepeatScreen.args
        ) {
            ReminderRepeat(modifier = modifier)
        }
        bottomSheet(
            route = ReminderRepeatIntervalScreen.routePattern,
            arguments = ReminderRepeatIntervalScreen.args
        ) {
            ReminderCustomPeriod(modifier = modifier)
        }
        dialog(
            route = ReminderApproveDialog.routePattern,
            arguments = ReminderApproveDialog.args
        ) {
            ApproveDialog()
        }
    }
}

internal val SavedStateHandle.noteId: Long
    get() = requireNotNull(get<Long>(ReminderScreen.noteIdArg))
internal val SavedStateHandle.isReturnToNoteSettings: Boolean
    get() = requireNotNull(get(ReminderApproveDialog.isReturnToNoteSettingsArg))