package com.gnest.remember.feature.reminder.customperiod

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gnest.remember.core.note.RepeatPeriod
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.designsystem.theme.asTextSource
import com.gnest.remember.core.navigation.Navigator
import com.gnest.remember.feature.reminder.displayName
import com.gnest.remember.feature.reminder.domain.ChangeReminderPeriodUseCase
import com.gnest.remember.feature.reminder.domain.ObserveCustomPeriodUseCase
import com.gnest.remember.feature.reminder.navigation.ReminderApproveDialog
import com.gnest.remember.feature.reminder.navigation.noteId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import javax.inject.Inject

@HiltViewModel
internal class ReminderCustomPeriodViewModel @Inject constructor(
    private val navigator: Navigator,
    private val handle: SavedStateHandle,
    observeCustomPeriodUseCase: ObserveCustomPeriodUseCase,
    private val changeReminderPeriodUseCase: ChangeReminderPeriodUseCase
) : ViewModel() {

    private val days = DayOfWeek.values().map { day -> day.displayName().asTextSource }

    val state: StateFlow<CustomIntervalState> = observeCustomPeriodUseCase(handle.noteId).map { period ->
        CustomIntervalState(
            days = days,
            selectedIndexes = period.days.map { it.ordinal }.toSet()
        )
    }.stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = CustomIntervalState(days, emptySet()))


    fun onBackClick() {
        navigator.popBack()
    }

    fun onCloseClick() {
        navigator.navigateTo(ReminderApproveDialog(handle.noteId))
    }

    fun onIntervalClicked(isChecked: Boolean, index: Int) {
        val indexes = state.value.selectedIndexes.toMutableSet()
        when {
            isChecked -> indexes.add(index)
            else -> indexes.remove(index)
        }
        val days = DayOfWeek.values().filterIndexed { dayIndex, _ -> dayIndex in indexes }
        viewModelScope.launch { changeReminderPeriodUseCase(handle.noteId, RepeatPeriod.Custom(days)) }
    }
}

@Stable
data class CustomIntervalState(val days: List<TextSource>, val selectedIndexes: Set<Int>)