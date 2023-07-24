package com.gnest.remember.core.database.di

import android.content.Context
import androidx.room.Room
import com.gnest.remember.core.common.network.Dispatcher
import com.gnest.remember.core.common.network.RememberDispatchers
import com.gnest.remember.core.database.NotesDatabase
import com.gnest.remember.core.database.dao.InterestingIdeaDao
import com.gnest.remember.core.database.migration.RealmMigration
import com.gnest.remember.core.database.migration.RealmToRoomMigration
import com.gnest.remember.core.database.model.old.MemoRealmFields
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

private const val MEMO_DATABASE_NAME = "notes_database"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideNotesDatabase(@ApplicationContext context: Context): NotesDatabase =
        Room.databaseBuilder(
            context,
            NotesDatabase::class.java,
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
        interestingIdeaDao: InterestingIdeaDao,
        @MainRealm mainRealmConfig: RealmConfiguration,
        @ArchivedRealm archivedRealmConfig: RealmConfiguration,
        @Dispatcher(RememberDispatchers.SINGLE)
        dispatcher: CoroutineDispatcher
    ): RealmToRoomMigration =
        RealmToRoomMigration(
            interestingIdeaDao,
            mainRealmConfig,
            archivedRealmConfig,
            dispatcher
        )
}