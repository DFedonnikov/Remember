package com.gnest.remember.feature.interestingidea

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gnest.remember.core.common.extensions.formatForNewNote
import com.gnest.remember.core.common.extensions.localDateTimeNow
import com.gnest.remember.core.designsystem.theme.AppColors
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.feature.interestingidea.domain.CreateNewInterestingIdeaUseCase
import com.gnest.remember.feature.interestingidea.domain.DeleteInterestingIdeaUseCase
import com.gnest.remember.feature.interestingidea.domain.GetInterestingIdeaUseCase
import com.gnest.remember.feature.interestingidea.domain.ObserveInterestingIdeaUseCase
import com.gnest.remember.feature.interestingidea.domain.SaveInterestingIdeaUseCase
import com.gnest.remember.feature.interestingidea.navigation.ideaId
import com.gnest.remember.core.navigation.Navigator
import com.gnest.remember.core.navigation.NoteSettingsScreen
import com.gnest.remember.core.note.Note
import com.gnest.remember.core.note.NoteColor
import com.gnest.remember.core.note.RepeatPeriod
import com.gnest.remember.core.noteuimapper.asUiColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(FlowPreview::class)
@HiltViewModel
class InterestingIdeaViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getIdeaUseCase: GetInterestingIdeaUseCase,
    observeIdeaUseCase: ObserveInterestingIdeaUseCase,
    private val createNewInterestingIdeaUseCase: CreateNewInterestingIdeaUseCase,
    private val saveIdeaUseCase: SaveInterestingIdeaUseCase,
    private val deleteIdeaUseCase: DeleteInterestingIdeaUseCase,
    private val navigator: Navigator
) : ViewModel() {

    init {
        viewModelScope.launch {
            val idea = when (val id = savedStateHandle.ideaId) {
                null -> createNewInterestingIdeaUseCase()
                else -> requireNotNull(getIdeaUseCase(id))
            }
            savedStateHandle.ideaId = idea.id
            localIdea.emit(idea)
            localIdea.debounce(1.toDuration(DurationUnit.SECONDS))
                .onEach { saveIdeaUseCase(it) }
                .launchIn(this)
        }
    }

    private val localIdea = MutableStateFlow(emptyIdea())

    val state: Flow<InterestingIdeaState> by lazy {
        localIdea.map { idea ->
            InterestingIdeaState(
                title = TextSource.Simple(idea.title),
                text = TextSource.Simple(idea.text),
                color = idea.color.asUiColor(),
                lastEdited = idea.lastEdited.formatForNewNote()
            )
        }
    }


    fun updateTitle(title: String) {
        localIdea.tryEmit(
            localIdea.value.copy(
                title = title,
                lastEdited = Clock.System.localDateTimeNow()
            )
        )
    }

    fun updateText(text: String) {
        localIdea.tryEmit(
            localIdea.value.copy(
                text = text,
                lastEdited = Clock.System.localDateTimeNow()
            )
        )
    }

    override fun onCleared() {
        with(localIdea.value) {
            if (title.isEmpty() && text.isEmpty() && savedStateHandle.ideaId == null) {
                deleteIdeaUseCase(id)
            }
        }
    }

    fun onBackClick() {
        navigator.popBack()
    }

    fun onMoreClick() {
        navigator.navigateTo(NoteSettingsScreen(requireNotNull(savedStateHandle.ideaId)))
    }
}

data class InterestingIdeaState(
    val title: TextSource = TextSource.Simple(""),
    val text: TextSource = TextSource.Simple(""),
    val color: Color = AppColors.White,
    val lastEdited: String = ""
)

internal fun emptyIdea() = Note.InterestingIdea(
    id = -1,
    title = "",
    text = "",
    color = NoteColor.WHITE,
    lastEdited = Clock.System.localDateTimeNow(),
    alarmDate = null,
    isAlarmSet = false,
    repeatPeriod = RepeatPeriod.Once
)