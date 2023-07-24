package com.gnest.remember.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gnest.remember.core.database.dao.InterestingIdeaDao
import com.gnest.remember.core.database.model.InterestingIdeaEntity
import com.gnest.remember.core.database.util.Converters

@Database(entities = [InterestingIdeaEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun interestingIdeaDao(): InterestingIdeaDao
}