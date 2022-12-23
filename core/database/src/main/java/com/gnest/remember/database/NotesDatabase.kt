package com.gnest.remember.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gnest.remember.database.dao.InterestingIdeaDao
import com.gnest.remember.database.model.InterestingIdeaEntity
import com.gnest.remember.database.util.Converters

@Database(entities = [InterestingIdeaEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun interestingIdeaDao(): InterestingIdeaDao
}