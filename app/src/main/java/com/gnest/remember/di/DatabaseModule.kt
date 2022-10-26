package com.gnest.remember.di

import android.content.Context
import androidx.room.Room
import com.gnest.remember.App
import com.gnest.remember.data.db.MemoDao
import com.gnest.remember.data.db.MemoDatabase
import com.gnest.remember.data.db.RealmToRoomMigration
import com.gnest.remember.model.db.data.MemoRealmFields
import com.gnest.remember.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.realm.Realm
import io.realm.RealmConfiguration
import javax.inject.Singleton

private const val MEMO_DATABASE_NAME = "memo_database"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideMemoDatabase(@ApplicationContext context: Context): MemoDatabase = Room.databaseBuilder(context, MemoDatabase::class.java, MEMO_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideMemoDao(database: MemoDatabase): MemoDao = database.memoDao()

    @Singleton
    @MainRealm
    @Provides
    fun provideMainRealm(): RealmConfiguration = requireNotNull(Realm.getDefaultConfiguration())

    @Singleton
    @ArchivedRealm
    @Provides
    fun provideArchivedRealm(): RealmConfiguration = requireNotNull(App.getConfigurationByName(MemoRealmFields.ARCHIVE_CONFIG_NAME))

    @Singleton
    @Provides
    fun provideRealmToRoomMigration(memoDao: MemoDao,
                                    @MainRealm mainRealmConfig: RealmConfiguration,
                                    @ArchivedRealm archivedRealmConfig: RealmConfiguration,
                                    dispatcherProvider: DispatcherProvider): RealmToRoomMigration =
            RealmToRoomMigration(memoDao, mainRealmConfig, archivedRealmConfig, dispatcherProvider)
}