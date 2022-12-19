package com.gnest.remember.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gnest.remember.database.dao.MemoDao
import com.gnest.remember.database.model.Memo
import com.gnest.remember.database.util.Converters

@Database(entities = [Memo::class], version = 1)
@TypeConverters(Converters::class)
abstract class MemoDatabase : RoomDatabase() {

    abstract fun memoDao(): MemoDao
}