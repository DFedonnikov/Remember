package com.gnest.remember.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface MemoListInteractor {

    fun getActiveMemos(): Flow<List<Memo>>
    fun getArchivedMemos(): Flow<List<Memo>>
    fun updatePositions(idsToPosition: Map<Int, Int>)
    fun archiveMemos(archivedIds: List<Int>)
    fun unarchiveMemos(unarchivedIds: List<Int>)
    fun removeMemos(removedIds: List<Int>)
}

class MemoListInteractorImpl constructor(private val repository: MemoRepository) : MemoListInteractor, CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    override fun getActiveMemos(): Flow<List<Memo>> {
        return repository.getActiveMemos().map { memos -> memos.sortedBy { it.position } }
    }

    override fun getArchivedMemos(): Flow<List<Memo>> {
        return repository.getArchivedMemos().map { memos -> memos.sortedBy { it.position } }
    }

    override fun updatePositions(idsToPosition: Map<Int, Int>) {
        repository.updatePositions(idsToPosition)
    }

    override fun archiveMemos(archivedIds: List<Int>) {
        launch {
            var startPosition = repository.getLastArchivedPosition()?.plus(1) ?: 0
            val idsToPosition = archivedIds.associateWith { startPosition++ }
            repository.setArchived(archivedIds, idsToPosition)
        }
    }

    override fun unarchiveMemos(unarchivedIds: List<Int>) {
        launch {
            var startPosition = repository.getLastUnarchivedPosition()?.plus(1) ?: 0
            val idsToPosition = unarchivedIds.associateWith { startPosition++ }
            repository.setUnarchived(unarchivedIds, idsToPosition)
        }
    }

    override fun removeMemos(removedIds: List<Int>) {
        repository.removeMemos(removedIds)
    }
}