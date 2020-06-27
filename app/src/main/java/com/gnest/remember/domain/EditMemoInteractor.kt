package com.gnest.remember.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface EditMemoInteractor {

    fun getMemos(isArchived: Boolean): Flow<List<Memo>>
    suspend fun saveMemo(memo: Memo): Int
    fun updateMemos(memos: List<Memo>)
    fun updatePositions(idsToPosition: Map<Int, Int>)
    suspend fun saveToCalendar(data: CalendarData): Boolean
    suspend fun removeCalendarEvent(id: Int): Boolean
}

class EditMemoInteractorImpl constructor(private val repository: MemoRepository) : EditMemoInteractor {

    override fun getMemos(isArchived: Boolean): Flow<List<Memo>> {
        val activeMemos = when {
            isArchived -> repository.getArchivedMemos()
            else -> repository.getActiveMemos()
        }
        return activeMemos.map { memos -> memos.sortedBy { it.position } }
    }

    override suspend fun saveMemo(memo: Memo): Int = memo.takeIf { it.id == 0 && it.text.isNotEmpty() }?.let { repository.createMemo(memo) }
            ?: 0

    override fun updateMemos(memos: List<Memo>) {
        memos.filter { it.id != 0 }.let {
            repository.updateAll(it)
        }
    }

    override fun updatePositions(idsToPosition: Map<Int, Int>) {
        repository.updatePositions(idsToPosition)
    }

    override suspend fun saveToCalendar(data: CalendarData): Boolean {
        val isEventExists = repository.isCalendarEventExists(data.id)
        val eventId = when {
            isEventExists -> repository.updateCalendarEvent(data)
            else -> repository.createCalendarEvent(data)
        }
        return eventId != 0L
    }

    override suspend fun removeCalendarEvent(id: Int): Boolean = repository.removeCalendarEvent(id)
}