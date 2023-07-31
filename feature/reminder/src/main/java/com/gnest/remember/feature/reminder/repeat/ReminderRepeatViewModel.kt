package com.gnest.remember.feature.reminder.repeat

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gnest.remember.core.note.RepeatPeriod
import com.gnest.remember.core.navigation.Navigator
import com.gnest.remember.feature.reminder.RepeatPeriodUi
import com.gnest.remember.feature.reminder.domain.ChangeReminderPeriodUseCase
import com.gnest.remember.feature.reminder.domain.ObserveNoteReminderInfoUseCase
import com.gnest.remember.feature.reminder.navigation.ReminderApproveDialog
import com.gnest.remember.feature.reminder.navigation.ReminderRepeatIntervalScreen
import com.gnest.remember.feature.reminder.navigation.noteId
import com.gnest.remember.feature.reminder.toDomainModel
import com.gnest.remember.feature.reminder.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ReminderRepeatViewModel @Inject constructor(
    private val navigator: Navigator,
    private val handle: SavedStateHandle,
    observeNoteReminderInfoUseCase: ObserveNoteReminderInfoUseCase,
    private val changeReminderPeriodUseCase: ChangeReminderPeriodUseCase
) : ViewModel() {

    private val repeatPeriod = observeNoteReminderInfoUseCase(handle.noteId).map { it.repeatPeriod }

    val state: Flow<ScreenState> = repeatPeriod.map { period ->
        ScreenState(
            days = listOf(
                RepeatPeriodUi.Once,
                RepeatPeriodUi.Daily,
                RepeatPeriodUi.Weekdays,
                RepeatPeriodUi.Weekend,
                (period as? RepeatPeriod.Custom)?.toUiModel() ?: RepeatPeriodUi.Custom()
            ),
            selectedIndex = period.toSelectedIndex()
        )
    }

    fun onBackClick() {
        navigator.popBack()
    }

    fun onCloseClick() {
        navigator.navigateTo(ReminderApproveDialog(handle.noteId))
    }

    fun onItemSelected(period: RepeatPeriodUi) {
        viewModelScope.launch { changeReminderPeriodUseCase(handle.noteId, period.toDomainModel()) }
    }

    fun onChangeCustomPeriodClick() {
        navigator.navigateTo(ReminderRepeatIntervalScreen(id = handle.noteId))
    }
}

@Stable
data class ScreenState(val days: List<RepeatPeriodUi>, val selectedIndex: Int)

fun RepeatPeriod.toSelectedIndex(): Int = when (this) {
    RepeatPeriod.Once -> 0
    RepeatPeriod.Daily -> 1
    RepeatPeriod.Weekdays -> 2
    RepeatPeriod.Weekend -> 3
    is RepeatPeriod.Custom -> 4
}