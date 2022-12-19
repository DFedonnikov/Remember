package com.gnest.remember.database.di

import android.content.Context
import androidx.room.Room
import com.gnest.remember.common.network.Dispatcher
import com.gnest.remember.common.network.RememberDispatchers
import com.gnest.remember.database.dao.MemoDao
import com.gnest.remember.database.migration.RealmMigration
import com.gnest.remember.database.migration.RealmToRoomMigration
import com.gnest.remember.database.model.old.MemoRealmFields
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

private const val MEMO_DATABASE_NAME = "memo_database"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideMemoDatabase(@ApplicationContext context: Context): com.gnest.remember.database.MemoDatabase =
        Room.databaseBuilder(
            context,
            com.gnest.remember.database.MemoDatabase::class.java,
            MEMO_DATABASE_NAME
        ).build()

    @Singleton
    @MainRealm
    @Provides
    fun provideMainRealm(): RealmConfiguration {
        val config = RealmConfiguration.Builder()
            .name(MemoRealmFields.DEFAULT_CONFIG_NAME)
            .schemaVersion(1)
            .migration(RealmMigration())
            .build()
        Realm.setDefaultConfiguration(config)
        return requireNotNull(Realm.getDefaultConfiguration())
    }

    @Singleton
    @ArchivedRealm
    @Provides
    fun provideArchivedRealm(): RealmConfiguration = requireNotNull(
        RealmConfiguration.Builder()
            .name(MemoRealmFields.ARCHIVE_CONFIG_NAME)
            .schemaVersion(1)
            .migration(RealmMigration())
            .build()
    )

    @Singleton
    @Provides
    fun provideRealmToRoomMigration(
        memoDao: MemoDao,
        @MainRealm mainRealmConfig: RealmConfiguration,
        @ArchivedRealm archivedRealmConfig: RealmConfiguration,
        @Dispatcher(RememberDispatchers.SINGLE)
        dispatcher: CoroutineDispatcher
    ): RealmToRoomMigration =
        RealmToRoomMigration(
            memoDao,
            mainRealmConfig,
            archivedRealmConfig,
            dispatcher
        )
}