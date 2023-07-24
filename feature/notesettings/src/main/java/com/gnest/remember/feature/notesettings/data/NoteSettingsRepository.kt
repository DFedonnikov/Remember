package com.gnest.remember.feature.notesettings.data

import com.gnest.remember.core.common.network.Dispatcher
import com.gnest.remember.core.common.network.RememberDispatchers
import com.gnest.remember.core.database.dao.InterestingIdeaDao
import com.gnest.remember.core.common.extensions.localDateTimeNow
import com.gnest.remember.core.database.model.ColorUpdate
import com.gnest.remember.core.note.NoteColor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

internal interface NoteSettingsRepository {

    fun observeNoteReminderDate(id: Long): Flow<LocalDateTime?>
    fun observeInterestingIdeaColor(id: Long): Flow<NoteColor>
    suspend fun saveNoteColor(id: Long, color: NoteColor)
}

internal class NoteSettingsRepositoryImpl @Inject constructor(
    private val interestingIdeaDao: InterestingIdeaDao,
    @Dispatcher(RememberDispatchers.IO)
    override val coroutineContext: CoroutineDispatcher
) : NoteSettingsRepository, CoroutineScope {

    override fun observeNoteReminderDate(id: Long): Flow<LocalDateTime?> = interestingIdeaDao.observeById(id)
        .map { it.reminderDate }
        .flowOn(coroutineContext)

    override fun observeInterestingIdeaColor(id: Long): Flow<NoteColor> = interestingIdeaDao.observeById(id)
        .map { it.color }
        .flowOn(coroutineContext)

    override suspend fun saveNoteColor(id: Long, color: NoteColor) = withContext(coroutineContext) {
        interestingIdeaDao.updateColor(ColorUpdate(id = id, color = color, lastEdited = Clock.System.localDateTimeNow()))
    }
}