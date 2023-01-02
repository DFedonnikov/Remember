package com.gnest.remember.notesettings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gnest.remember.core.ui.lists.ColorSelectorDefaults
import com.gnest.remember.core.ui.lists.ColorSelectorItem
import com.gnest.remember.common.extensions.asNoteColor
import com.gnest.remember.common.extensions.isEqualTo
import com.gnest.remember.navigation.Navigator
import com.gnest.remember.notesettings.domain.ObserveNoteColorUseCase
import com.gnest.remember.notesettings.domain.SaveNoteColorUseCase
import com.gnest.remember.notesettings.navigation.noteId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteSettingsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    observeNoteColorUseCase: ObserveNoteColorUseCase,
    private val saveNoteColorUseCase: SaveNoteColorUseCase,
    private val navigator: Navigator
) : ViewModel() {

    val backgroundColors: Flow<List<ColorSelectorItem>> =
        observeNoteColorUseCase(savedStateHandle.noteId).map { noteColor ->
            ColorSelectorDefaults.colorSelectorList.map {
                it.isSelected.value = noteColor isEqualTo it.color
                it
            }
        }

    fun onCloseClick() {
        navigator.popBack()
    }

    fun onItemClick(item: ColorSelectorItem) {
        viewModelScope.launch {
            saveNoteColorUseCase(savedStateHandle.noteId, item.color.asNoteColor())
        }
    }
}