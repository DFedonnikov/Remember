package com.gnest.remember.data

import androidx.room.*
import com.gnest.remember.data.dto.MemoDTO
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {

    @Query("SELECT * FROM MemoDTO WHERE isArchived = 0")
    fun getActive(): Flow<List<MemoDTO>>

    @Query("SELECT * FROM MemoDTO WHERE isArchived = 1")
    fun getArchived(): Flow<List<MemoDTO>>

    @Insert
    fun insertAll(memos: List<MemoDTO>)

    @Update
    fun update(memos: List<MemoDTO>)

    @Query("UPDATE MemoDTO SET position = :position WHERE id = :id")
    fun updatePosition(id: Int, position: Int)

    @Query("SELECT MAX(position) FROM MemoDTO WHERE isArchived = 1")
    suspend fun getLastArchivedPosition(): Int?

    @Transaction
    fun archiveAndAdjustPositions(archivedIds: List<Int>, idsToPosition: Map<Int, Int>) {
        setArchived(archivedIds)
        idsToPosition.forEach { (id, position) -> updatePosition(id, position) }
    }

    @Query("UPDATE MemoDTO SET isArchived = 1 WHERE id IN (:ids)")
    fun setArchived(ids: List<Int>)

    @Query("SELECT MAX(position) FROM MemoDTO WHERE isArchived = 0")
    fun getLastUnarchivedPosition(): Int?

    @Transaction
    fun unarchiveAndAdjustPositions(unarchivedIds: List<Int>, idsToPosition: Map<Int, Int>) {
        setUnarchived(unarchivedIds)
        idsToPosition.forEach { (id, position) -> updatePosition(id, position) }
    }

    @Query("UPDATE MemoDTO SET isArchived = 0 WHERE id IN (:ids)")
    fun setUnarchived(ids: List<Int>)

    @Query("DELETE FROM MemoDTO WHERE id IN (:ids)")
    fun remove(ids: List<Int>)
}