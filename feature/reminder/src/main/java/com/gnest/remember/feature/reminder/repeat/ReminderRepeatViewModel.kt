package com.gnest.remember.feature.reminder.repeat

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.gnest.remember.core.note.RepeatPeriod
import com.gnest.remember.core.navigation.Navigator
import com.gnest.remember.core.navigation.NoteSettingsScreen
import com.gnest.remember.feature.reminder.RepeatPeriodUi
import com.gnest.remember.feature.reminder.domain.ChangeReminderPeriodUseCase
import com.gnest.remember.feature.reminder.domain.ObserveNoteReminderInfoUseCase
import com.gnest.remember.feature.reminder.navigation.ReminderRepeatIntervalScreen
import com.gnest.remember.feature.reminder.navigation.noteId
import com.gnest.remember.feature.reminder.toDomainModel
import com.gnest.remember.feature.reminder.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
internal class ReminderRepeatViewModel @Inject constructor(
    private val navigator: Navigator,
    private val handle: SavedStateHandle,
    observeNoteReminderInfoUseCase: ObserveNoteReminderInfoUseCase,
    private val changeReminderPeriodUseCase: ChangeReminderPeriodUseCase
) : ViewModel() {


    val state: Flow<ScreenState> = observeNoteReminderInfoUseCase(handle.noteId).map {
        ScreenState(
            days = listOf(
                RepeatPeriodUi.Once,
                RepeatPeriodUi.Daily,
                RepeatPeriodUi.Weekdays,
                RepeatPeriodUi.Weekend,
                (it.repeatPeriod as? RepeatPeriod.Custom)?.toUiModel() ?: RepeatPeriodUi.Custom()
            ),
            selectedIndex = it.repeatPeriod.toSelectedIndex()
        )
    }

    fun onBackClick() {
        navigator.popBack()
    }

    fun onCloseClick() {
        navigator.popBackTo(NoteSettingsScreen(handle.noteId), isInclusive = true)
    }

    fun onItemSelected(period: RepeatPeriodUi) {
        changeReminderPeriodUseCase(handle.noteId, period.toDomainModel())
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