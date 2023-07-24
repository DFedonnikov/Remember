package com.gnest.remember.feature.reminder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gnest.remember.core.common.extensions.formatForDate
import com.gnest.remember.core.common.extensions.formatForTime
import com.gnest.remember.core.common.extensions.localDateTimeNow
import com.gnest.remember.core.common.extensions.nearestRoundHour
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.feature.reminder.domain.ChangeReminderDateUseCase
import com.gnest.remember.feature.reminder.domain.ObserveNoteReminderInfoUseCase
import com.gnest.remember.feature.reminder.navigation.noteId
import com.gnest.remember.core.navigation.Navigator
import com.gnest.remember.core.navigation.NoteSettingsScreen
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
    observeNoteReminderInfoUseCase: ObserveNoteReminderInfoUseCase,
    private val changeReminderDateUseCase: ChangeReminderDateUseCase,
    private val navigator: Navigator
) : ViewModel() {

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
        navigator.popBackTo(NoteSettingsScreen(handle.noteId), isInclusive = true)
    }

    fun onBackClick() {
        navigator.popBack()
    }

    fun onReminderChanged(isChecked: Boolean) {
        viewModelScope.launch {
            changeReminderDateUseCase(handle.noteId, if (isChecked) Clock.System.localDateTimeNow() else null)
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