package com.gnest.remember.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gnest.remember.core.database.model.ActiveNoPositionUpdate
import com.gnest.remember.core.database.model.InterestingIdeaEntity
import com.gnest.remember.core.database.model.ColorUpdate
import com.gnest.remember.core.database.model.ReminderUpdate
import com.gnest.remember.core.database.model.RepeatPeriodUpdate
import kotlinx.coroutines.flow.Flow

@Dao
interface InterestingIdeaDao {
    @Query("SELECT * FROM InterestingIdeaEntity")
    suspend fun getAll(): List<InterestingIdeaEntity>

    @Query("SELECT * FROM InterestingIdeaEntity")
    fun observeAll(): Flow<List<InterestingIdeaEntity>>

    @Query("SELECT * FROM InterestingIdeaEntity WHERE id = :id")
    suspend fun getById(id: Long): InterestingIdeaEntity?

    @Query("SELECT * FROM InterestingIdeaEntity WHERE id = :id")
    fun observeById(id: Long): Flow<InterestingIdeaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: InterestingIdeaEntity): Long

    @Update(entity = InterestingIdeaEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entity: ActiveNoPositionUpdate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg notes: InterestingIdeaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<InterestingIdeaEntity>)

    @Query("DELETE FROM InterestingIdeaEntity where id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(id) FROM InterestingIdeaEntity")
    fun countNotes(): Flow<Int>

    @Update(entity = InterestingIdeaEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateColor(entity: ColorUpdate)

    @Update(entity = InterestingIdeaEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateReminderDate(entity: ReminderUpdate)

    @Update(entity = InterestingIdeaEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun updateRepeatPeriod(repeatPeriodUpdate: RepeatPeriodUpdate)

}