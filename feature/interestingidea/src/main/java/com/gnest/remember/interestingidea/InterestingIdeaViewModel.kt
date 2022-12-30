package com.gnest.remember.interestingidea

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gnest.remember.common.extensions.formatForNewNote
import com.gnest.remember.common.extensions.localDateTimeNow
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.database.model.NoteColor
import com.gnest.remember.interestingidea.domain.CreateNewInterestingIdeaUseCase
import com.gnest.remember.interestingidea.domain.DeleteInterestingIdeaUseCase
import com.gnest.remember.interestingidea.domain.GetInterestingIdeaUseCase
import com.gnest.remember.interestingidea.domain.InterestingIdea
import com.gnest.remember.interestingidea.domain.SaveInterestingIdeaUseCase
import com.gnest.remember.interestingidea.navigation.ideaId
import com.gnest.remember.navigation.Navigator
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
    private val createNewInterestingIdeaUseCase: CreateNewInterestingIdeaUseCase,
    private val saveIdeaUseCase: SaveInterestingIdeaUseCase,
    private val deleteIdeaUseCase: DeleteInterestingIdeaUseCase,
    private val navigator: Navigator
) : ViewModel() {

    private val localIdea = MutableStateFlow(emptyIdea())
    val state: Flow<InterestingIdeaState> = localIdea.map { idea ->
        InterestingIdeaState(
            title = TextSource.Simple(idea.title),
            text = TextSource.Simple(idea.text),
            lastEdited = idea.lastEdited.formatForNewNote()
        )
    }

    init {
        viewModelScope.launch {
            val idea = when (val id = savedStateHandle.ideaId) {
                null -> createNewInterestingIdeaUseCase()
                else -> requireNotNull(getIdeaUseCase(id))
            }
            localIdea.emit(idea)
            localIdea.debounce(1.toDuration(DurationUnit.SECONDS))
                .onEach { saveIdeaUseCase(it) }
                .launchIn(this)
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
}

data class InterestingIdeaState(
    val title: TextSource = TextSource.Simple(""),
    val text: TextSource = TextSource.Simple(""),
    val lastEdited: String = ""
)

internal fun emptyIdea() = InterestingIdea(
    id = -1,
    title = "",
    text = "",
    color = NoteColor.WHITE,
    lastEdited = Clock.System.localDateTimeNow(),
    alarmDate = null,
    isAlarmSet = false
)