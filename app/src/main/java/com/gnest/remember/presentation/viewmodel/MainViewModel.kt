package com.gnest.remember.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gnest.remember.domain.MemoRepository
import com.gnest.remember.presentation.mappers.MemoMapper
import com.gnest.remember.presentation.ui.state.MemoNotificationState
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MemoRepository,
                    private val mapper: MemoMapper) : ViewModel() {

    private val _activeMemoNotification: MutableLiveData<Event<MemoNotificationState>> = MutableLiveData()
    private val _archivedMemoNotification: MutableLiveData<Event<MemoNotificationState>> = MutableLiveData()

    val activeMemoNotification: MutableLiveData<Event<MemoNotificationState>> = _activeMemoNotification
    val archivedMemoNotification: MutableLiveData<Event<MemoNotificationState>> = _archivedMemoNotification

    fun onOpenFromNotification(id: Int) {
        viewModelScope.launch {
            val memo = repository.getMemo(id)
            val stateEvent = Event(MemoNotificationState(memo.id, memo.position, memo.isArchived))
            when {
                memo.isArchived -> _archivedMemoNotification.value = stateEvent
                else -> _activeMemoNotification.value = stateEvent
            }
            repository.update(memo.copy(alarmDate = null))
        }
    }
}