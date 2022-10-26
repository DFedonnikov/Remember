package com.gnest.remember.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gnest.remember.data.db.entity.Memo

@Database(entities = [Memo::class], version = 1)
@TypeConverters(Converters::class)
abstract class MemoDatabase : RoomDatabase() {

    abstract fun memoDao(): MemoDao
}