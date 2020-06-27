package com.gnest.remember.presentation.viewmodel

import androidx.lifecycle.*
import com.gnest.remember.domain.EditMemoInteractor
import com.gnest.remember.presentation.ResourceProvider
import com.gnest.remember.presentation.mappers.MemoMapper
import com.gnest.remember.presentation.ui.memolist.MemoItem
import com.gnest.remember.presentation.ui.state.CalendarEventSaveState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditMemoListViewModel(private val interactor: EditMemoInteractor,
                            private val memoMapper: MemoMapper,
                            private val resourceProvider: ResourceProvider) : ViewModel() {

    private val changedItems = mutableMapOf<Int, MemoItem>()
    private val _list: LiveData<List<MemoItem>>
        get() = interactor.getMemos(isArchived).map {
            with(memoMapper.mapDomain(it)) {
                when (id) {
                    0 -> toMutableList().apply {
                        add(0, MemoItem())
                    }
                    else -> this
                }
            }
        }.asLiveData(viewModelScope.coroutineContext)
    private val _scrollToLiveData = MutableLiveData<Unit>()
    private val _calendarEventSaveState = MutableLiveData<Event<CalendarEventSaveState>>()
    private var id: Int = 0
    private var isArchived = false
    private var updaterJob: Job? = null


    val list: LiveData<List<MemoItem>> by lazy { _list }
    val scrollToLiveData: LiveData<Unit> = _scrollToLiveData
    val calendarEventSaveState: LiveData<Event<CalendarEventSaveState>> = _calendarEventSaveState

    fun init(itemId: Int, isArchived: Boolean) {
        id = itemId
        this.isArchived = isArchived
        launchUpdater()
    }

    private fun launchUpdater() {
        updaterJob = viewModelScope.launch {
            ticker(delayMillis = 200, initialDelayMillis = 0)
                    .consumeAsFlow()
                    .flowOn(Dispatchers.IO)
                    .catch { }
                    .filter { changedItems.isNotEmpty() }
                    .collect {
                        if (id == 0) {
                            saveMemo()
                            updatePositions()
                        }
                        updateMemos()
                        changedItems.clear()
                    }
        }
    }

    private suspend fun saveMemo() {
        changedItems.values.find { it.id == 0 }?.let {
            id = withContext(viewModelScope.coroutineContext) { interactor.saveMemo(memoMapper.mapUi(it)) }
        }

    }

    private fun updatePositions() {
        val changedPositions = list.value?.mapIndexedNotNull { index, item ->
            when {
                index != item.position -> item.id to index
                else -> null
            }
        }?.associate { it }
        changedPositions?.let { interactor.updatePositions(it) }
    }

    private fun updateMemos() {
        interactor.updateMemos(memoMapper.mapUi(changedItems.values.toList()))
    }

    fun onItemChanged(item: MemoItem) {
        changedItems[item.id] = item
    }

    fun onListLayoutComplete() {
        _scrollToLiveData.value = Unit
    }

    override fun onCleared() {
        super.onCleared()
        updaterJob?.cancel()
        if (changedItems.isNotEmpty()) {
            viewModelScope.launch { saveMemo() }
            updateMemos()
        }
    }

    fun saveToCalendar(item: MemoItem) {
        viewModelScope.launch {
            val isSaved = interactor.saveToCalendar(memoMapper.mapToCalendarData(item))
            val message = when {
                isSaved -> resourceProvider.saveCalendarEventSuccess
                else -> resourceProvider.saveCalendarEventFailure
            }
            _calendarEventSaveState.value = Event(CalendarEventSaveState(message))
        }
    }

    fun removeFromCalendar(id: Int) {
        viewModelScope.launch {
            val isRemoved = interactor.removeCalendarEvent(id)
            val message = when {
                isRemoved -> resourceProvider.removeCalendarEventSuccess
                else -> resourceProvider.removeCalendarEventFailure
            }
            _calendarEventSaveState.value = Event(CalendarEventSaveState(message))
        }
    }
}