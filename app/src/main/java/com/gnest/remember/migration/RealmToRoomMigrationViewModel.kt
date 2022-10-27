package com.gnest.remember.migration

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gnest.remember.data.db.RealmToRoomMigration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val SAVED_UI_STATE_KEY = "savedUiStateKey"

@HiltViewModel
class RealmToRoomMigrationViewModel @Inject constructor(savedStateHandle: SavedStateHandle,
                                                        private val migration: RealmToRoomMigration) : ViewModel() {

    val uiState = savedStateHandle.getStateFlow(SAVED_UI_STATE_KEY, MigrationUiState.Loading)
    private val intentFlow = MutableSharedFlow<MigrationIntent>()

    init {
        viewModelScope.launch {
            intentFlow
                    .flatMapMerge { mapIntents(it) }
                    .catch {
                        println("MIGRATION: intentFlow $it")
                    }
                    .onEach { savedStateHandle[SAVED_UI_STATE_KEY] = it }
                    .launchIn(this)
        }
        acceptIntent(MigrationIntent.StartMigration)
    }

    private fun mapIntents(intent: MigrationIntent): Flow<MigrationUiState> {
        return when (intent) {
            is MigrationIntent.StartMigration -> startMigration()
            is MigrationIntent.OpenMemos -> emptyFlow()
        }
    }

    private fun startMigration(): Flow<MigrationUiState> = flow {
        emit(MigrationUiState.Loading)
        val beforeMigration = System.currentTimeMillis()
        migration.migrateFromRealmToRoom()
        val afterMigration = System.currentTimeMillis()
        //For more smooth UX and animation cycle
        if (afterMigration - beforeMigration < 2000) {
            delay(2000)
        }
        emit(MigrationUiState.Migrated)
    }
            .catch {
                println("MIGRATION: error: $it")
                emit(MigrationUiState.Error)
            }

    fun acceptIntent(intent: MigrationIntent) {
        viewModelScope.launch { intentFlow.emit(intent) }
    }

}

interface MigrationUiState {

    @Parcelize
    object Loading : MigrationUiState, Parcelable

    @Parcelize
    object Migrated : MigrationUiState, Parcelable

    @Parcelize
    object Error : MigrationUiState, Parcelable

}

sealed interface MigrationIntent {

    object StartMigration : MigrationIntent
    object OpenMemos : MigrationIntent
}