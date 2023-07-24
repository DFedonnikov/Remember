package com.gnest.remember.feature.interestingidea.data

import com.gnest.remember.core.common.extensions.localDateTimeNow
import com.gnest.remember.core.common.network.Dispatcher
import com.gnest.remember.core.common.network.RememberDispatchers
import com.gnest.remember.core.database.dao.InterestingIdeaDao
import com.gnest.remember.core.database.model.ActiveNoPositionUpdate
import com.gnest.remember.core.database.model.InterestingIdeaEntity
import com.gnest.remember.core.note.Note
import com.gnest.remember.core.note.NoteColor
import com.gnest.remember.core.note.RepeatPeriod
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import javax.inject.Inject

interface InterestingIdeaRepository {

    suspend fun getIdeaById(id: Long): Note.InterestingIdea?

    fun observeIdeaById(id: Long): Flow<Note.InterestingIdea>
    suspend fun createNewIdea(): Note.InterestingIdea
    suspend fun updateIdea(idea: Note.InterestingIdea)
    fun deleteById(id: Long)
    fun observeInterestingIdeas(): Flow<List<Note.InterestingIdea>>
}

class InterestingIdeaRepositoryImpl @Inject constructor(
    private val dao: InterestingIdeaDao,
    @Dispatcher(RememberDispatchers.IO)
    override val coroutineContext: CoroutineDispatcher
) :
    InterestingIdeaRepository, CoroutineScope {

    override suspend fun getIdeaById(id: Long): Note.InterestingIdea? = withContext(coroutineContext) {
        dao.getById(id)?.asDomainModel()
    }

    override fun observeIdeaById(id: Long): Flow<Note.InterestingIdea> = dao.observeById(id)
        .map { it.asDomainModel() }
        .flowOn(coroutineContext)

    override suspend fun createNewIdea(): Note.InterestingIdea = withContext(coroutineContext) {
        val idea = InterestingIdeaEntity(
            id = 0,
            title = "",
            text = "",
            position = 0,
            color = NoteColor.WHITE,
            lastEdited = Clock.System.localDateTimeNow(),
            reminderDate = null,
            isReminderSet = false,
            isFinished = false,
            repeatPeriod = RepeatPeriod.Once
        )
        val newId = dao.insert(idea)
        idea.asDomainModel(newId)
    }

    override suspend fun updateIdea(idea: Note.InterestingIdea) = withContext(coroutineContext) {
        dao.update(idea.asActiveNoPositionUpdateEntity())
    }

    override fun deleteById(id: Long) {
        launch { dao.deleteById(id) }
    }

    override fun observeInterestingIdeas(): Flow<List<Note.InterestingIdea>> = dao.observeAll()
        .map { ideas -> ideas.map { it.asDomainModel() } }
        .flowOn(coroutineContext)
}

private fun InterestingIdeaEntity.asDomainModel(id: Long = this.id): Note.InterestingIdea =
    Note.InterestingIdea(
        id = id,
        title = title,
        text = text,
        color = color,
        lastEdited = lastEdited,
        alarmDate = reminderDate,
        isAlarmSet = isReminderSet,
        repeatPeriod = repeatPeriod
    )

private fun Note.InterestingIdea.asActiveNoPositionUpdateEntity(): ActiveNoPositionUpdate =
    ActiveNoPositionUpdate(id, title, text, color, lastEdited, alarmDate, isAlarmSet)