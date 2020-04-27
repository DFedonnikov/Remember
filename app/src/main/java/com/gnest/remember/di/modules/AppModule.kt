package com.gnest.remember.di.modules

import androidx.room.Room
import com.gnest.remember.*
import com.gnest.remember.data.MemoDatabase
import com.gnest.remember.data.datasources.ArchiveLocalDataSource
import com.gnest.remember.data.datasources.LocalDataSource
import com.gnest.remember.data.datasources.LocalDataSourceImpl
import com.gnest.remember.data.datasources.MainLocalDataSource
import com.gnest.remember.data.mappers.MemoDTOMapper
import com.gnest.remember.data.mappers.MemoDTOMapperImpl
import com.gnest.remember.data.repository.MemoRepositoryImpl
import com.gnest.remember.domain.*
import com.gnest.remember.model.db.data.MemoRealmFields
import com.gnest.remember.presentation.mappers.MemoMapper
import com.gnest.remember.presentation.mappers.MemoMapperImpl
import com.gnest.remember.presentation.viewmodel.EditMemoListViewModel
import com.gnest.remember.presentation.viewmodel.ActiveListViewModel
import com.gnest.remember.presentation.viewmodel.ArchivedListViewModel
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val MEMO_DATABASE = "memo-database"

val appModule = module {

    single { Room.databaseBuilder(androidApplication(), MemoDatabase::class.java, MEMO_DATABASE).build() }
    single<MemoRepository> { MemoRepositoryImpl(get(), get(), get()) }
    single<LocalDataSource> { LocalDataSourceImpl(get()) }

    single<MemoListInteractor> { MemoListInteractorImpl(get()) }
    single<EditMemoInteractor> { EditMemoInteractorImpl(get()) }

    single<MemoDTOMapper> { MemoDTOMapperImpl() }
    single<MemoMapper> { MemoMapperImpl() }

    factory { CoroutineScope(Job() + Dispatchers.IO) }

    factory<Realm>(named(MAIN_REALM)) { Realm.getDefaultInstance() }
    factory<Realm>(named(ARCHIVE_REALM)) { Realm.getInstance(App.getConfigurationByName(MemoRealmFields.ARCHIVE_CONFIG_NAME)) }

    factory(named(MAIN_OLD_DATASOURCE)) { MainLocalDataSource(get(named(MAIN_REALM)), get(named(ARCHIVE_REALM))) }
    factory(named(ARCHIVE_OLD_DATASOURCE)) { ArchiveLocalDataSource(get(named(ARCHIVE_REALM)), get(named(MAIN_REALM))) }

    viewModel { ActiveListViewModel(get(), get()) }
    viewModel { EditMemoListViewModel(get(), get()) }
    viewModel { ArchivedListViewModel(get(), get()) }

}