package com.gnest.remember.di.modules

import com.gnest.remember.App
import com.gnest.remember.model.db.data.MemoRealmFields
import dagger.Module
import dagger.Provides
import io.realm.Realm
import javax.inject.Named
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Named(MAIN_DB)
    fun provideMainDB(): Realm = Realm.getDefaultInstance()

    @Provides
    @Named(ARCHIVE_DB)
    fun provideArchiveDB(): Realm = Realm.getInstance(App.getConfigurationByName(MemoRealmFields.ARCHIVE_CONFIG_NAME))

    companion object {

        const val MAIN_DB = "Main db"
        const val ARCHIVE_DB = "Archive_db"
    }
}