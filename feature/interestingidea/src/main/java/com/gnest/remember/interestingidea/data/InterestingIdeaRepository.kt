package com.gnest.remember.interestingidea.data

import com.gnest.remember.common.network.Dispatcher
import com.gnest.remember.common.network.RememberDispatchers
import com.gnest.remember.database.dao.InterestingIdeaDao
import com.gnest.remember.database.model.ActiveNoPositionUpdate
import com.gnest.remember.database.model.InterestingIdeaEntity
import com.gnest.remember.database.model.NoteColor
import com.gnest.remember.interestingidea.domain.InterestingIdea
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface InterestingIdeaRepository {

    suspend fun getIdeaById(id: Long): InterestingIdea?
    suspend fun createNewIdea(): InterestingIdea
    suspend fun updateIdea(idea: InterestingIdea)
    fun deleteById(id: Long)
}

class InterestingIdeaRepositoryImpl @Inject constructor(
    private val dao: InterestingIdeaDao,
    @Dispatcher(RememberDispatchers.IO)
    override val coroutineContext: CoroutineDispatcher
) :
    InterestingIdeaRepository, CoroutineScope {

    override suspend fun getIdeaById(id: Long): InterestingIdea? = withContext(coroutineContext) {
        dao.getById(id)?.asDomainModel()
    }

    override suspend fun createNewIdea(): InterestingIdea = withContext(coroutineContext) {
        val idea = InterestingIdeaEntity(
            id = 0,
            title = "",
            text = "",
            position = 0,
            NoteColor.WHITE,
            alarmDate = null,
            isAlarmSet = false,
            isFinished = false
        )
        val newId = dao.insert(idea)
        idea.asDomainModel(newId)
    }

    override suspend fun updateIdea(idea: InterestingIdea) = withContext(coroutineContext) {
        dao.update(idea.asActiveNoPositionUpdateEntity())
    }

    override fun deleteById(id: Long) {
        launch { dao.deleteById(id) }
    }
}

private fun InterestingIdeaEntity.asDomainModel(id: Long = this.id): InterestingIdea =
    InterestingIdea(
        id = id,
        title = title,
        text = text,
        color = color,
        alarmDate = alarmDate,
        isAlarmSet = isAlarmSet
    )

private fun InterestingIdea.asActiveNoPositionUpdateEntity(): ActiveNoPositionUpdate =
    ActiveNoPositionUpdate(id, title, text, color, alarmDate, isAlarmSet)
