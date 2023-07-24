package com.gnest.remember.feature.reminder.date

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gnest.remember.core.common.extensions.localDateTimeNow
import com.gnest.remember.core.navigation.Navigator
import com.gnest.remember.core.navigation.NoteSettingsScreen
import com.gnest.remember.core.ui.pickers.DateUiState
import com.gnest.remember.feature.reminder.DateScreenState
import com.gnest.remember.feature.reminder.domain.ChangeReminderDateUseCase
import com.gnest.remember.feature.reminder.domain.ObserveNoteReminderInfoUseCase
import com.gnest.remember.feature.reminder.navigation.noteId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
internal class ReminderDateViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    observeNoteReminderInfoUseCase: ObserveNoteReminderInfoUseCase,
    val changeReminderDateUseCase: ChangeReminderDateUseCase,
    private val navigator: Navigator,
) : ViewModel() {

    private val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    private val currentReminderDateTime = observeNoteReminderInfoUseCase(handle.noteId)
        .map { it.date ?: Clock.System.localDateTimeNow() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    private val initialReminderDateTime = currentReminderDateTime.take(2)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    private val years = List(100) { now.year + it }
    val state = combineTransform(
        initialReminderDateTime,
        currentReminderDateTime,
    ) { _, currentReminderDateTime ->
        currentReminderDateTime?.let { currentDateTime ->
            val minimalDay = when {
                now.month == currentDateTime.month && now.year == currentDateTime.year -> now.dayOfMonth
                else -> 1
            }
            val maxDaysInSelectedMonth = (minimalDay..currentDateTime.month.maxLength()).toList()
            val monthsInSelectedYear = when {
                now.month >= currentDateTime.month && now.year == currentDateTime.year -> Month.values().takeLast(12 - currentDateTime.monthNumber + 1)
                else -> Month.values().toList()
            }
            emit(
                DateScreenState(
                    DateUiState(
                        selectedDate = currentDateTime.date,
                        days = maxDaysInSelectedMonth,
                        months = monthsInSelectedYear,
                        years = years
                    )
                )
            )
        }
    }

    fun onBackClick() {
        navigator.popBack()
    }

    fun onCloseClick() {
        initialReminderDateTime.value?.let { changeReminderDateUseCase(handle.noteId, it) }
        navigator.popBackTo(NoteSettingsScreen(handle.noteId), isInclusive = true)
    }

    fun onDateChanged(date: LocalDate) {
        val selectedDate = date.coerceAtLeast(now.date)
        initialReminderDateTime.value?.let { initial ->
            val dateTime = LocalDateTime(selectedDate, initial.time)
            changeReminderDateUseCase(handle.noteId, dateTime)
        }
    }
}