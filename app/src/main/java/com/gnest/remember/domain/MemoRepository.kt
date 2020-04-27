package com.gnest.remember.domain

import kotlinx.coroutines.flow.Flow

interface MemoRepository {

    fun getActiveMemos(): Flow<List<Memo>>
    fun getArchivedMemos(): Flow<List<Memo>>
    fun insertAll(memos: List<Memo>)
    fun updateAll(memos: List<Memo>)
    fun updatePositions(idsToPosition: Map<Int, Int>)
    suspend fun getLastArchivedPosition(): Int?
    fun setArchived(archivedIds: List<Int>, idsToPosition: Map<Int, Int>)
    suspend fun getLastUnarchivedPosition(): Int?
    fun setUnarchived(ids: List<Int>, idsToPosition: Map<Int, Int>)
    fun removeMemos(removedIds: List<Int>)
}
