package com.gnest.remember.data.repository

import com.gnest.remember.data.datasources.LocalDataSource
import com.gnest.remember.data.mappers.MemoDTOMapper
import com.gnest.remember.domain.Memo
import com.gnest.remember.domain.MemoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class MemoRepositoryImpl constructor(private val localDataSource: LocalDataSource,
                                     private val mapper: MemoDTOMapper,
                                     private val ioScope: CoroutineScope) : MemoRepository {


    override fun getActiveMemos(): Flow<List<Memo>> = localDataSource.getActiveMemos()
            .map { mapper.mapDto(it) }

    override fun getArchivedMemos(): Flow<List<Memo>> = localDataSource.getArchivedMemos()
            .map { mapper.mapDto(it) }

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
}