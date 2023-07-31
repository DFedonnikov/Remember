package com.gnest.remember.feature.reminder.date

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gnest.remember.core.common.extensions.localDateTimeNow
import com.gnest.remember.core.navigation.Navigator
import com.gnest.remember.core.ui.pickers.DateUiState
import com.gnest.remember.feature.reminder.DateScreenState
import com.gnest.remember.feature.reminder.domain.ChangeReminderDateUseCase
import com.gnest.remember.feature.reminder.domain.ObserveNoteReminderInfoUseCase
import com.gnest.remember.feature.reminder.navigation.ReminderApproveDialog
import com.gnest.remember.feature.reminder.navigation.noteId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
internal class ReminderDateViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    observeNoteReminderInfoUseCase: ObserveNoteReminderInfoUseCase,
    private val changeReminderDateUseCase: ChangeReminderDateUseCase,
    private val navigator: Navigator,
) : ViewModel() {

    private val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    private val currentReminderDateTime = observeNoteReminderInfoUseCase(handle.noteId)
        .map { it.date ?: Clock.System.localDateTimeNow() }

    private val years = List(100) { now.year + it }
    val state = currentReminderDateTime.map { currentReminderDateTime ->
        val minimalDay = when {
            now.month == currentReminderDateTime.month && now.year == currentReminderDateTime.year -> now.dayOfMonth
            else -> 1
        }
        val maxDaysInSelectedMonth = (minimalDay..currentReminderDateTime.month.maxLength()).toList()
        val monthsInSelectedYear = when {
            now.month >= currentReminderDateTime.month && now.year == currentReminderDateTime.year -> Month.values()
                .takeLast(12 - currentReminderDateTime.monthNumber + 1)

            else -> Month.values().toList()
        }
        DateScreenState(
            DateUiState(
                selectedDate = currentReminderDateTime.date,
                days = maxDaysInSelectedMonth,
                months = monthsInSelectedYear,
                years = years
            )
        )
    }

    fun onBackClick() {
        navigator.popBack()
    }

    fun onCloseClick() {
        navigator.navigateTo(ReminderApproveDialog(handle.noteId))
    }

    fun onDateChanged(date: LocalDate) {
        val selectedDate = date.coerceAtLeast(now.date)
        viewModelScope.launch { changeReminderDateUseCase.changeReminderDate(handle.noteId, selectedDate) }
    }
}