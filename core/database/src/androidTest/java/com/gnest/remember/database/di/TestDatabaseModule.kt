package com.gnest.remember.database.di

import android.content.Context
import androidx.room.Room
import com.gnest.remember.database.MemoDatabase
import com.gnest.remember.database.migration.RealmMigration
import com.gnest.remember.database.model.old.MemoRealmFields
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
    fun provideMemoDatabase(@ApplicationContext context: Context): MemoDatabase =
        Room.inMemoryDatabaseBuilder(context, MemoDatabase::class.java).build()

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