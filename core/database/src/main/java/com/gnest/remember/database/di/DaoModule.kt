package com.gnest.remember.database.di

import com.gnest.remember.database.NotesDatabase
import com.gnest.remember.database.dao.InterestingIdeaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Singleton
    @Provides
    fun provideMemoDao(database: NotesDatabase): InterestingIdeaDao =
        database.interestingIdeaDao()
}