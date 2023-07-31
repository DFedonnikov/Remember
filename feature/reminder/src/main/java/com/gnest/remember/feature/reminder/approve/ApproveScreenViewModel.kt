package com.gnest.remember.feature.reminder.approve

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.navigation.Navigator
import com.gnest.remember.core.navigation.NoteSettingsScreen
import com.gnest.remember.feature.reminder.R
import com.gnest.remember.feature.reminder.domain.RestoreInitialReminderUseCase
import com.gnest.remember.feature.reminder.domain.SetResultAlarmUseCase
import com.gnest.remember.feature.reminder.navigation.isReturnToNoteSettings
import com.gnest.remember.feature.reminder.navigation.noteId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApproveScreenViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val setResultAlarmUseCase: SetResultAlarmUseCase,
    private val restoreInitialReminderUseCase: RestoreInitialReminderUseCase,
    private val navigator: Navigator
) : ViewModel() {

    val state = flow {
        emit(
            ApproveDialogState(
                title = TextSource.Resource(R.string.approve_reminder_dialog_title),
                text = TextSource.Resource(R.string.approve_reminder_dialog_text)
            )
        )
    }

    fun onApproveClick() {
        viewModelScope.launch {
            setResultAlarmUseCase(handle.noteId)
            navigator.popBackTo(NoteSettingsScreen(handle.noteId), isInclusive = !handle.isReturnToNoteSettings)
        }
    }

    fun onDismissClick() {
        viewModelScope.launch {
            restoreInitialReminderUseCase(handle.noteId)
            navigator.popBackTo(NoteSettingsScreen(handle.noteId), isInclusive = !handle.isReturnToNoteSettings)
        }
    }
}

data class ApproveDialogState(val title: TextSource, val text: TextSource)