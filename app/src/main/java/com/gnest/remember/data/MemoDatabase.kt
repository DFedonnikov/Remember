package com.gnest.remember.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gnest.remember.data.dto.MemoDTO

@Database(entities = [MemoDTO::class], version = 1)
@TypeConverters(Converters::class)
abstract class MemoDatabase : RoomDatabase() {

    abstract fun memoDao(): MemoDao
}