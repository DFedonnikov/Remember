package com.gnest.remember.feature.reminder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gnest.remember.core.common.extensions.formatForDate
import com.gnest.remember.core.common.extensions.formatForTime
import com.gnest.remember.core.common.extensions.twentyFourHoursFromNow
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.feature.reminder.domain.ChangeReminderDateUseCase
import com.gnest.remember.feature.reminder.domain.ObserveNoteReminderInfoUseCase
import com.gnest.remember.feature.reminder.navigation.noteId
import com.gnest.remember.core.navigation.Navigator
import com.gnest.remember.core.note.RepeatPeriod
import com.gnest.remember.feature.reminder.domain.ChangeReminderPeriodUseCase
import com.gnest.remember.feature.reminder.domain.SaveInitialReminderDateUseCase
import com.gnest.remember.feature.reminder.navigation.ReminderApproveDialog
import com.gnest.remember.feature.reminder.navigation.ReminderDateScreen
import com.gnest.remember.feature.reminder.navigation.ReminderRepeatScreen
import com.gnest.remember.feature.reminder.navigation.ReminderTimeScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
internal class ReminderRouteViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    saveInitialReminderDateUseCase: SaveInitialReminderDateUseCase,
    observeNoteReminderInfoUseCase: ObserveNoteReminderInfoUseCase,
    private val changeReminderDateUseCase: ChangeReminderDateUseCase,
    private val changeReminderPeriodUseCase: ChangeReminderPeriodUseCase,
    private val navigator: Navigator
) : ViewModel() {

    init {
        saveInitialReminderDateUseCase(handle.noteId)
    }

    val state = observeNoteReminderInfoUseCase(handle.noteId).map { info ->
        val isEnabled = info.date != null
        ReminderScreenState(
            isReminderEnabled = isEnabled,
            date = info.date?.let { TextSource.Simple(it.formatForDate()) } ?: TextSource.Resource(R.string.not_set),
            time = info.date?.let { TextSource.Simple(it.formatForTime()) } ?: TextSource.Resource(R.string.not_set),
            repeat = info.repeatPeriod.toUiModel().title
        )
    }

    fun onCloseClick() {
        navigator.navigateTo(ReminderApproveDialog(handle.noteId))
    }

    fun onBackClick() {
        navigator.navigateTo(ReminderApproveDialog(handle.noteId, isReturnToNoteSettings = true))
    }

    fun onReminderChanged(isChecked: Boolean) {
        viewModelScope.launch {
            val dateTime = if (isChecked) Clock.System.twentyFourHoursFromNow() else null
            changeReminderDateUseCase.changeReminderDateTime(handle.noteId, dateTime)
            changeReminderPeriodUseCase(handle.noteId, RepeatPeriod.Once)
        }
    }

    fun onDateChangeClicked() {
        navigator.navigateTo(ReminderDateScreen(handle.noteId))
    }

    fun onTimeChangeClicked() {
        navigator.navigateTo(ReminderTimeScreen(handle.noteId))
    }

    fun onRepeatClicked() {
        navigator.navigateTo(ReminderRepeatScreen(handle.noteId))
    }

}

data class ReminderScreenState(
    val isReminderEnabled: Boolean,
    val date: TextSource,
    val time: TextSource,
    val repeat: TextSource
)