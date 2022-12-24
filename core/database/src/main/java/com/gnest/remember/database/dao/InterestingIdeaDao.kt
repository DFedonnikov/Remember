package com.gnest.remember.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gnest.remember.database.model.ActiveNoPositionUpdate
import com.gnest.remember.database.model.InterestingIdeaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InterestingIdeaDao {
    @Query("SELECT * FROM InterestingIdeaEntity")
    suspend fun getAll(): List<InterestingIdeaEntity>

    @Query("SELECT * FROM InterestingIdeaEntity")
    fun observeAll(): Flow<List<InterestingIdeaEntity>>

    @Query("SELECT * FROM InterestingIdeaEntity WHERE id = :id")
    suspend fun getById(id: Long): InterestingIdeaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: InterestingIdeaEntity): Long

    @Update(entity = InterestingIdeaEntity::class)
    suspend fun update(entity: ActiveNoPositionUpdate)

    @Insert
    suspend fun insertAll(vararg notes: InterestingIdeaEntity)

    @Insert
    suspend fun insertAll(notes: List<InterestingIdeaEntity>)

    @Query("DELETE FROM InterestingIdeaEntity where id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(id) FROM InterestingIdeaEntity")
    fun countNotes(): Flow<Int>

}