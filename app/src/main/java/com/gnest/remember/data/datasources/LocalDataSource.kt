package com.gnest.remember.data.datasources

import com.gnest.remember.data.MemoDatabase
import com.gnest.remember.data.dto.MemoDTO
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    fun getActiveMemos(): Flow<List<MemoDTO>>
    fun getArchivedMemos(): Flow<List<MemoDTO>>
    fun insertAll(memos: List<MemoDTO>)
    fun updateAll(memos: List<MemoDTO>)
    fun updatePosition(id: Int, position: Int)
    suspend fun getLastArchivedPosition(): Int?
    fun archiveAndAdjustPositions(archivedIds: List<Int>, idsToPosition: Map<Int, Int>)
    suspend fun getLastUnarchivedPosition(): Int?
    fun unarchiveAndAdjuastPositions(ids: List<Int>, idsToPosition: Map<Int, Int>)
    fun remove(ids: List<Int>)
}

class LocalDataSourceImpl constructor(private val database: MemoDatabase) : LocalDataSource {

    override fun getActiveMemos(): Flow<List<MemoDTO>> = database.memoDao().getActive()

    override fun getArchivedMemos(): Flow<List<MemoDTO>> = database.memoDao().getArchived()

    override fun insertAll(memos: List<MemoDTO>) = database.memoDao().insertAll(memos)

    override fun updateAll(memos: List<MemoDTO>) = database.memoDao().update(memos)

    override fun updatePosition(id: Int, position: Int) = database.memoDao().updatePosition(id, position)

    override suspend fun getLastArchivedPosition(): Int? = database.memoDao().getLastArchivedPosition()

    override fun archiveAndAdjustPositions(archivedIds: List<Int>, idsToPosition: Map<Int, Int>) =
            database.memoDao().archiveAndAdjustPositions(archivedIds, idsToPosition)

    override suspend fun getLastUnarchivedPosition(): Int? = database.memoDao().getLastUnarchivedPosition()

    override fun unarchiveAndAdjuastPositions(ids: List<Int>, idsToPosition: Map<Int, Int>) =
            database.memoDao().unarchiveAndAdjustPositions(ids, idsToPosition)

    override fun remove(ids: List<Int>) = database.memoDao().remove(ids)
}