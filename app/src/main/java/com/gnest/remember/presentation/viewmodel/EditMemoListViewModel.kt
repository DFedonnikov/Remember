package com.gnest.remember.presentation.viewmodel

import androidx.lifecycle.*
import com.gnest.remember.domain.EditMemoInteractor
import com.gnest.remember.presentation.mappers.MemoMapper
import com.gnest.remember.presentation.ui.memolist.MemoItem
import kotlinx.coroutines.flow.map

class EditMemoListViewModel(private val interactor: EditMemoInteractor,
                            private val memoMapper: MemoMapper) : ViewModel() {

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
    private var id: Int = 0
    private var isArchived = false

    val list: LiveData<List<MemoItem>>
        get() = _list
    private val _scrollToLiveData = MutableLiveData<Unit>()
    val scrollToLiveData: LiveData<Unit> = _scrollToLiveData

    fun init(itemId: Int, isArchived: Boolean) {
        id = itemId
        this.isArchived = isArchived
    }

    fun onItemChanged(item: MemoItem) {
        changedItems[item.id] = item
    }


    fun onBackPressed(currentList: List<MemoItem>) {
        interactor.saveMemos(memoMapper.mapUi(changedItems.values.toList()))
        if (id == 0) {
            val changedPositions = currentList.mapIndexedNotNull { index, item ->
                when {
                    index != item.position -> item.id to index
                    else -> null
                }
            }.associate { it }
            interactor.updatePositions(changedPositions)
            changedItems.clear()
        }
    }


    fun onListLayoutComplete() {
        _scrollToLiveData.value = Unit
    }
}