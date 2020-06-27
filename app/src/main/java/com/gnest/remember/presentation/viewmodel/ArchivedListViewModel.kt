package com.gnest.remember.presentation.viewmodel

import androidx.lifecycle.*
import com.gnest.remember.domain.MemoListInteractor
import com.gnest.remember.presentation.ResourceProvider
import com.gnest.remember.presentation.mappers.MemoMapper
import com.gnest.remember.presentation.ui.memolist.MemoItem
import com.gnest.remember.presentation.ui.state.DismissRemovedState
import com.gnest.remember.presentation.ui.state.DismissUnarchivedState
import kotlinx.coroutines.flow.map

class ArchivedListViewModel(private val interactor: MemoListInteractor,
                            private val mapper: MemoMapper,
                            private val resourceProvider: ResourceProvider) : ViewModel() {

    private val outerList: LiveData<List<MemoItem>> = interactor.getArchivedMemos().map { mapper.mapDomain(it) }.asLiveData(viewModelScope.coroutineContext)
    private val localList = MutableLiveData<List<MemoItem>>()
    private val _list = MediatorLiveData<List<MemoItem>>()
    private val dismissUnarchivedItems: MutableList<MemoItem> = mutableListOf()
    private val dismissRemovedItems: MutableList<MemoItem> = mutableListOf()
    private val _dismissUnarchivedLiveData = MutableLiveData<Event<DismissUnarchivedState>>()
    private val _dismissRemovedLiveData = MutableLiveData<Event<DismissRemovedState>>()
    private val _removeNotificationsAlarmLiveData = MutableLiveData<Event<List<Int>>>()

    val list: LiveData<List<MemoItem>> = _list
    val dismissUnarchivedLiveData: LiveData<Event<DismissUnarchivedState>> = _dismissUnarchivedLiveData
    val dismissRemovedLiveData: LiveData<Event<DismissRemovedState>> = _dismissRemovedLiveData
    val removeNotificationsAlarmLiveData = _removeNotificationsAlarmLiveData

    init {
        _list.addSource(outerList) { _list.value = it }
        _list.addSource(localList) { _list.value = it }
    }

    fun onItemUnarchive(item: MemoItem?) {
        item?.let {
            dismissUnarchivedItems.add(it)
            _dismissUnarchivedLiveData.value = DismissUnarchivedState(resourceProvider.getMemosUnarchivedMessage(dismissUnarchivedItems.size)).asEvent()
        }
    }

    fun onItemsUnarchive(selectedItems: List<MemoItem>) {
        dismissUnarchivedItems.addAll(selectedItems)
        localList.value = _list.value?.minus(dismissUnarchivedItems)
        _dismissUnarchivedLiveData.value = DismissUnarchivedState(resourceProvider.getMemosUnarchivedMessage(dismissUnarchivedItems.size)).asEvent()
    }

    fun onItemDismissUnarchivedCancel() {
        dismissUnarchivedItems.clear()
        _list.postValue(outerList.value)
    }

    fun onItemDismissUnarchivedTimeout() {
        interactor.unarchiveMemos(dismissUnarchivedItems.map { it.id })
        dismissUnarchivedItems.clear()
    }

    fun onScreenClose(data: List<MemoItem>) {
        updatePositions(data)
    }


    private fun updatePositions(data: List<MemoItem>) {
        val changed = data.mapIndexedNotNull { index, item ->
            when {
                index != item.position -> item.id to index
                else -> null
            }
        }.associate { it }
        interactor.updatePositions(changed)
    }

    fun onItemsRemove(selectedItems: List<MemoItem>) {
        dismissRemovedItems.addAll(selectedItems)
        localList.value = _list.value?.minus(dismissRemovedItems)
        _dismissRemovedLiveData.value = DismissRemovedState((resourceProvider.getMemosRemovedMessage(dismissRemovedItems.size))).asEvent()
    }

    fun onItemDismissRemovedCancel() {
        dismissRemovedItems.clear()
        _list.postValue(outerList.value)
    }

    fun onItemDismissRemovedTimeout() {
        val idsToRemove = dismissRemovedItems.map { it.id }
        interactor.removeMemos(idsToRemove)
        _removeNotificationsAlarmLiveData.value = Event(idsToRemove)
        dismissRemovedItems.clear()
    }
}