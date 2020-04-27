package com.gnest.remember.presentation.viewmodel

import androidx.lifecycle.*
import com.gnest.remember.domain.MemoListInteractor
import com.gnest.remember.presentation.mappers.MemoMapper
import com.gnest.remember.presentation.ui.memolist.MemoItem
import com.gnest.remember.presentation.ui.state.DismissArchivedState
import com.gnest.remember.presentation.ui.state.DismissRemovedState
import kotlinx.coroutines.flow.map

class ActiveListViewModel(private val interactor: MemoListInteractor,
                          private val mapper: MemoMapper) : ViewModel() {

    private val outerList: LiveData<List<MemoItem>> = interactor.getActiveMemos().map { mapper.mapDomain(it) }.asLiveData(viewModelScope.coroutineContext)
    private val localList = MutableLiveData<List<MemoItem>>()
    private val _list = MediatorLiveData<List<MemoItem>>()
    private val dismissArchivedItems: MutableList<MemoItem> = mutableListOf()
    private val dismissRemovedItems: MutableList<MemoItem> = mutableListOf()
    private val _dismissArchivedLiveData = MutableLiveData<DismissArchivedState>()
    private val _dismissRemovedLiveData = MutableLiveData<DismissRemovedState>()

    val list: LiveData<List<MemoItem>> = _list
    val dismissArchivedLiveData: LiveData<DismissArchivedState> = _dismissArchivedLiveData
    val dismissRemovedLiveData: LiveData<DismissRemovedState> = _dismissRemovedLiveData

    init {
        _list.addSource(outerList) { _list.value = it }
        _list.addSource(localList) { _list.value = it }
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

    fun onItemArchive(item: MemoItem?) {
        item?.let {
            dismissArchivedItems.add(it)
            _dismissArchivedLiveData.value = DismissArchivedState("${dismissArchivedItems.size}")
        }
    }

    fun onItemDismissArchivedTimeout() {
        interactor.archiveMemos(dismissArchivedItems.map { it.id })
        dismissArchivedItems.clear()
    }

    fun onItemDismissArchivedCancel() {
        dismissArchivedItems.clear()
        _list.postValue(outerList.value)
    }

    fun onItemsArchive(selectedItems: List<MemoItem>) {
        dismissArchivedItems.addAll(selectedItems)
        localList.value = _list.value?.minus(dismissArchivedItems)
        _dismissArchivedLiveData.value = DismissArchivedState("${dismissArchivedItems.size}")
    }

    fun onItemsRemove(selectedItems: List<MemoItem>) {
        dismissRemovedItems.addAll(selectedItems)
        localList.value = _list.value?.minus(dismissRemovedItems)
        _dismissRemovedLiveData.value = DismissRemovedState(("${dismissRemovedItems.size}"))
    }

    fun onItemDismissRemovedCancel() {
        dismissRemovedItems.clear()
        _list.postValue(outerList.value)
    }

    fun onItemDismissRemovedTimeout() {
        interactor.removeMemos(dismissRemovedItems.map { it.id })
        dismissRemovedItems.clear()
    }
}