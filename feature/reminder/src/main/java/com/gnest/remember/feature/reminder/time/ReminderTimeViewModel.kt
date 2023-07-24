package com.gnest.remember.feature.reminder.time

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gnest.remember.core.common.extensions.localDateTimeNow
import com.gnest.remember.core.navigation.Navigator
import com.gnest.remember.core.navigation.NoteSettingsScreen
import com.gnest.remember.feature.reminder.TimeScreenState
import com.gnest.remember.feature.reminder.domain.ChangeReminderDateUseCase
import com.gnest.remember.feature.reminder.domain.ObserveNoteReminderInfoUseCase
import com.gnest.remember.feature.reminder.navigation.noteId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import javax.inject.Inject

@HiltViewModel
internal class ReminderTimeViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    observeNoteReminderInfo: ObserveNoteReminderInfoUseCase,
    val changeReminderDateUseCase: ChangeReminderDateUseCase,
    private val navigator: Navigator,
) : ViewModel() {

    private var isSaveOnExit = true
    private val isBackEnabled = MutableStateFlow(true)
    private val selectedTime = observeNoteReminderInfo(handle.noteId)
        .map { it.date ?: Clock.System.localDateTimeNow() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    private val initialReminderDateTime = selectedTime
        .take(2)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val state = combineTransform(initialReminderDateTime, selectedTime, isBackEnabled) { _, selectedTime, isBackEnabled ->
        selectedTime?.time?.let { emit(TimeScreenState(it, isBackEnabled)) }
    }

    fun onBackClick() {
        navigator.popBack()
    }

    fun onCloseClick() {
        isSaveOnExit = false
        navigator.popBackTo(NoteSettingsScreen(handle.noteId), isInclusive = true)
    }

    fun onTimeChanged(time: LocalTime) {
        initialReminderDateTime.value?.let { initial ->
            val dateTime = if (isSaveOnExit) LocalDateTime(initial.date, time) else initial
            val isValidDateTime = dateTime > Clock.System.localDateTimeNow()
            if (isValidDateTime) {
                changeReminderDateUseCase(handle.noteId, dateTime)
            }
            isBackEnabled.tryEmit(isValidDateTime)
        }
    }
}