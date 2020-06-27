package com.gnest.remember.domain

import kotlinx.coroutines.flow.Flow

interface MemoRepository {

    fun getActiveMemos(): Flow<List<Memo>>
    fun getArchivedMemos(): Flow<List<Memo>>
    suspend fun createMemo(memo: Memo): Int
    fun insertAll(memos: List<Memo>)
    fun updateAll(memos: List<Memo>)
    fun update(memo: Memo)
    fun updatePositions(idsToPosition: Map<Int, Int>)
    suspend fun getLastArchivedPosition(): Int?
    fun setArchived(archivedIds: List<Int>, idsToPosition: Map<Int, Int>)
    suspend fun getLastUnarchivedPosition(): Int?
    fun setUnarchived(ids: List<Int>, idsToPosition: Map<Int, Int>)
    fun removeMemos(removedIds: List<Int>)
    suspend fun getMemo(id: Int): Memo
    suspend fun isCalendarEventExists(id: Int): Boolean
    suspend fun updateCalendarEvent(data: CalendarData): Long
    suspend fun createCalendarEvent(data: CalendarData): Long
    suspend fun removeCalendarEvent(id: Int): Boolean
}
