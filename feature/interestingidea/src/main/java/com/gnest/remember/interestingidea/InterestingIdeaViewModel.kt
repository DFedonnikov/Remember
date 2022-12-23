package com.gnest.remember.interestingidea

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gnest.remember.core.designsystem.theme.TextSource
import com.gnest.remember.database.model.NoteColor
import com.gnest.remember.interestingidea.domain.CreateNewInterestingIdeaUseCase
import com.gnest.remember.interestingidea.domain.DeleteInterestingIdeaUseCase
import com.gnest.remember.interestingidea.domain.GetInterestingIdeaUseCase
import com.gnest.remember.interestingidea.domain.InterestingIdea
import com.gnest.remember.interestingidea.domain.SaveInterestingIdeaUseCase
import com.gnest.remember.interestingidea.navigation.ideaId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
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
    private val deleteIdeaUseCase: DeleteInterestingIdeaUseCase
) : ViewModel() {

    private val localIdea = MutableStateFlow(emptyIdea())
    val state: Flow<InterestingIdeaState> = localIdea.map { idea ->
        InterestingIdeaState(TextSource.Simple(idea.title), TextSource.Simple(idea.text))
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
        localIdea.tryEmit(localIdea.value.copy(title = title))
    }

    fun updateText(text: String) {
        localIdea.tryEmit(localIdea.value.copy(text = text))
    }

    override fun onCleared() {
        with(localIdea.value) {
            if (title.isEmpty() && text.isEmpty() && savedStateHandle.ideaId == null) {
                deleteIdeaUseCase(id)
            }
        }
    }
}

data class InterestingIdeaState(
    val title: TextSource = TextSource.Simple(""),
    val text: TextSource = TextSource.Simple("")
)

internal fun emptyIdea() = InterestingIdea(-1, "", "", NoteColor.WHITE, null, false)