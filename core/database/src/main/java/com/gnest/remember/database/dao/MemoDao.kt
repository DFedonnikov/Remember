package com.gnest.remember.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gnest.remember.database.model.Memo

@Dao
interface MemoDao {
    @Query("SELECT * FROM Memo")
    suspend fun getAll(): List<Memo>

    @Insert
    suspend fun insertAll(vararg memos: Memo)

    @Insert
    suspend fun insertAll(memos: List<Memo>)
}