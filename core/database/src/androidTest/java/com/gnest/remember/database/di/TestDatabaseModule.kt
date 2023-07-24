package com.gnest.remember.database.di

import android.content.Context
import androidx.room.Room
import com.gnest.remember.core.database.NotesDatabase
import com.gnest.remember.core.database.di.ArchivedRealm
import com.gnest.remember.core.database.di.DatabaseModule
import com.gnest.remember.core.database.di.MainRealm
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.realm.Realm
import io.realm.RealmConfiguration
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestDatabaseModule {

    @Singleton
    @Provides
    fun provideNotesDatabase(@ApplicationContext context: Context): NotesDatabase =
        Room.inMemoryDatabaseBuilder(context, NotesDatabase::class.java).build()

    @Singleton
    @MainRealm
    @Provides
    fun provideMainRealmConfiguration(): RealmConfiguration = requireNotNull(
        RealmConfiguration.Builder().inMemory().name("test-main-realm").build()
    )

    @Singleton
    @ArchivedRealm
    @Provides
    fun provideArchivedRealmConfiguration(): RealmConfiguration = requireNotNull(
        RealmConfiguration.Builder().inMemory().name("test-archived-realm").build()
    )

    @Singleton
    @MainRealm
    @Provides
    fun provideMainRealm(@MainRealm config: RealmConfiguration): Realm = Realm.getInstance(config)

    @Singleton
    @ArchivedRealm
    @Provides
    fun provideArchiveRealm(@ArchivedRealm config: RealmConfiguration): Realm =
        Realm.getInstance(config)

}