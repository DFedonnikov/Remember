package com.gnest.remember.data.repository

import com.gnest.remember.data.CalendarProvider
import com.gnest.remember.data.datasources.LocalDataSource
import com.gnest.remember.data.mappers.MemoDTOMapper
import com.gnest.remember.domain.CalendarData
import com.gnest.remember.domain.Memo
import com.gnest.remember.domain.MemoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class MemoRepositoryImpl constructor(private val localDataSource: LocalDataSource,
                                     private val calendarProvider: CalendarProvider,
                                     private val mapper: MemoDTOMapper,
                                     private val ioScope: CoroutineScope) : MemoRepository {


    override fun getActiveMemos(): Flow<List<Memo>> = localDataSource.getActiveMemos()
            .map { mapper.mapDto(it) }

    override fun getArchivedMemos(): Flow<List<Memo>> = localDataSource.getArchivedMemos()
            .map { mapper.mapDto(it) }

    override suspend fun createMemo(memo: Memo): Int {
        return localDataSource.createMemo(mapper.mapDomain(memo))
    }

    override fun insertAll(memos: List<Memo>) {
        ioScope.launch {
            localDataSource.insertAll(mapper.mapDomain(memos))
        }
    }

    override fun updateAll(memos: List<Memo>) {
        ioScope.launch {
            localDataSource.updateAll(mapper.mapDomain(memos))
        }
    }

    override fun update(memo: Memo) {
        ioScope.launch {
            localDataSource.update(mapper.mapDomain(memo))
        }
    }

    override fun updatePositions(idsToPosition: Map<Int, Int>) {
        ioScope.launch {
            idsToPosition.forEach { (id, position) ->
                localDataSource.updatePosition(id, position)
            }
        }
    }

    override suspend fun getLastArchivedPosition(): Int? {
        return localDataSource.getLastArchivedPosition()
    }

    override fun setArchived(archivedIds: List<Int>, idsToPosition: Map<Int, Int>) {
        ioScope.launch {
            localDataSource.archiveAndAdjustPositions(archivedIds, idsToPosition)
        }
    }

    override suspend fun getLastUnarchivedPosition(): Int? {
        return localDataSource.getLastUnarchivedPosition()
    }

    override fun setUnarchived(ids: List<Int>, idsToPosition: Map<Int, Int>) {
        ioScope.launch {
            localDataSource.unarchiveAndAdjuastPositions(ids, idsToPosition)
        }
    }

    override fun removeMemos(removedIds: List<Int>) {
        ioScope.launch {
            localDataSource.remove(removedIds)
        }
    }

    override suspend fun getMemo(id: Int): Memo = mapper.mapDto(localDataSource.getMemo(id))

    override suspend fun isCalendarEventExists(id: Int): Boolean = calendarProvider.isEventExists(id)

    override suspend fun updateCalendarEvent(data: CalendarData): Long = calendarProvider.updateCalendarEvent(data)

    override suspend fun createCalendarEvent(data: CalendarData): Long = calendarProvider.createCalendarEvent(data)

    override suspend fun removeCalendarEvent(id: Int): Boolean = calendarProvider.removeCalendarEvent(id)
}