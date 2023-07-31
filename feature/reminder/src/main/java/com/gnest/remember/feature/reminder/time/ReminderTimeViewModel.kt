package com.gnest.remember.feature.reminder.time

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gnest.remember.core.common.extensions.localDateTimeNow
import com.gnest.remember.core.navigation.Navigator
import com.gnest.remember.feature.reminder.TimeScreenState
import com.gnest.remember.feature.reminder.domain.ChangeReminderDateUseCase
import com.gnest.remember.feature.reminder.domain.ObserveNoteReminderInfoUseCase
import com.gnest.remember.feature.reminder.navigation.ReminderApproveDialog
import com.gnest.remember.feature.reminder.navigation.noteId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import javax.inject.Inject

@HiltViewModel
internal class ReminderTimeViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    observeNoteReminderInfoUseCase: ObserveNoteReminderInfoUseCase,
    private val changeReminderDateUseCase: ChangeReminderDateUseCase,
    private val navigator: Navigator,
) : ViewModel() {

    private val isBackEnabled = MutableStateFlow(true)
    private val selectedTime = observeNoteReminderInfoUseCase(handle.noteId)
        .map { it.date ?: Clock.System.localDateTimeNow() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val state = combineTransform(selectedTime, isBackEnabled) { selectedDateTime, isBackEnabled ->
        selectedDateTime?.time?.let {
            val isDateTimeValid = selectedDateTime > Clock.System.localDateTimeNow()
            emit(TimeScreenState(it, isBackEnabled && isDateTimeValid))
        }
    }

    fun onBackClick() {
        navigator.popBack()
    }

    fun onCloseClick() {
        navigator.navigateTo(ReminderApproveDialog(handle.noteId))
    }

    fun onTimeChanged(time: LocalTime) {
        viewModelScope.launch {
            val isValidDateTime = changeReminderDateUseCase.changeReminderTime(handle.noteId, time)
            isBackEnabled.tryEmit(isValidDateTime)
        }
    }
}