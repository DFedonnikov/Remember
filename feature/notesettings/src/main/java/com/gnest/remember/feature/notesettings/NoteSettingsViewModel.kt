package com.gnest.remember.feature.notesettings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gnest.remember.core.ui.lists.ColorSelectorDefaults
import com.gnest.remember.core.ui.lists.ColorSelectorItem
import com.gnest.remember.core.common.extensions.formatForDateTime
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.core.navigation.Navigator
import com.gnest.remember.core.noteuimapper.asNoteColor
import com.gnest.remember.core.noteuimapper.isEqualTo
import com.gnest.remember.feature.notesettings.domain.ObserveNoteColorUseCase
import com.gnest.remember.feature.notesettings.domain.ObserveNoteReminderDateUseCase
import com.gnest.remember.feature.notesettings.domain.SaveNoteColorUseCase
import com.gnest.remember.feature.notesettings.navigation.ScreenDependency
import com.gnest.remember.feature.notesettings.navigation.noteId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class NoteSettingsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    observeNoteColorUseCase: ObserveNoteColorUseCase,
    observeNoteReminderDateUseCase: ObserveNoteReminderDateUseCase,
    private val saveNoteColorUseCase: SaveNoteColorUseCase,
    private val screenDependency: ScreenDependency,
    private val navigator: Navigator
) : ViewModel() {

    private val backgroundColors: Flow<List<ColorSelectorItem>> =
        observeNoteColorUseCase(savedStateHandle.noteId).map { noteColor ->
            ColorSelectorDefaults.colorSelectorList.map {
                it.isSelected.value = noteColor isEqualTo it.color
                it
            }
        }
    private val reminderDateTime = observeNoteReminderDateUseCase(savedStateHandle.noteId)
    val state = combine(backgroundColors, reminderDateTime) { colors, dateTime ->
        val dateTimeFormatted = dateTime?.let { TextSource.Simple(it.formatForDateTime()) } ?: TextSource.Resource(R.string.not_set)
        ScreenState(colors, dateTimeFormatted)
    }

    fun onCloseClick() {
        navigator.popBack()
    }

    fun onItemClick(item: ColorSelectorItem) {
        viewModelScope.launch {
            saveNoteColorUseCase(savedStateHandle.noteId, item.color.asNoteColor())
        }
    }

    fun onSetReminderClicked() {
        navigator.navigateTo(screenDependency.getReminderScreen(requireNotNull(savedStateHandle.noteId)))
    }
}

internal data class ScreenState(val backgroundColors: List<ColorSelectorItem>, val dateTime: TextSource)