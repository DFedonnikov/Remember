package com.gnest.remember.notesettings.data

import com.gnest.remember.common.network.Dispatcher
import com.gnest.remember.common.network.RememberDispatchers
import com.gnest.remember.database.dao.InterestingIdeaDao
import com.gnest.remember.common.domain.NoteColor
import com.gnest.remember.common.extensions.localDateTimeNow
import com.gnest.remember.database.model.ColorUpdate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import javax.inject.Inject

interface NoteSettingsRepository {

    fun observeInterestingIdea(id: Long): Flow<NoteColor>
    suspend fun saveNoteColor(id: Long, color: NoteColor)
}

class NoteSettingsRepositoryImpl @Inject constructor(
    private val interestingIdeaDao: InterestingIdeaDao,
    @Dispatcher(RememberDispatchers.IO)
    override val coroutineContext: CoroutineDispatcher
) :
    NoteSettingsRepository, CoroutineScope {

    override fun observeInterestingIdea(id: Long): Flow<NoteColor> =
        interestingIdeaDao.observeById(id).map { it.color }
            .flowOn(coroutineContext)

    override suspend fun saveNoteColor(id: Long, color: NoteColor) {
        withContext(coroutineContext) {
            interestingIdeaDao.updateColor(ColorUpdate(id, color, Clock.System.localDateTimeNow()))
        }
    }
}