package com.gnest.remember.core.database.di

import com.gnest.remember.core.database.NotesDatabase
import com.gnest.remember.core.database.dao.InterestingIdeaDao
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