package com.gnest.remember.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface EditMemoInteractor {

    fun getMemos(isArchived: Boolean): Flow<List<Memo>>
    fun saveMemos(memos: List<Memo>)
    fun updatePositions(idsToPosition: Map<Int, Int>)
}

class EditMemoInteractorImpl constructor(private val repository: MemoRepository) : EditMemoInteractor {

    override fun getMemos(isArchived: Boolean): Flow<List<Memo>> {
        val activeMemos = when {
            isArchived -> repository.getArchivedMemos()
            else -> repository.getActiveMemos()
        }
        return activeMemos.map { memos -> memos.sortedBy { it.position } }
    }

    override fun saveMemos(memos: List<Memo>) {
        memos.find { it.id == 0 && it.text.isNotEmpty() }?.let { repository.insertAll(listOf(it)) }
        memos.filter { it.id != 0 }.takeIf { it.isNotEmpty() }?.let {
            repository.updateAll(it)
        }
    }

    override fun updatePositions(idsToPosition: Map<Int, Int>) {
        repository.updatePositions(idsToPosition)
    }
}