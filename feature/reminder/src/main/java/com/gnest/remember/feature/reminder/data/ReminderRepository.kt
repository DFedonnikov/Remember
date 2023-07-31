package com.gnest.remember.feature.reminder.data

import com.gnest.remember.core.common.extensions.localDateTimeNow
import com.gnest.remember.core.common.network.Dispatcher
import com.gnest.remember.core.common.network.RememberDispatchers
import com.gnest.remember.core.database.dao.InterestingIdeaDao
import com.gnest.remember.core.database.model.InterestingIdeaEntity
import com.gnest.remember.core.database.model.ReminderUpdate
import com.gnest.remember.core.database.model.RepeatPeriodUpdate
import com.gnest.remember.core.note.RepeatPeriod
import com.gnest.remember.feature.reminder.domain.ReminderInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

interface ReminderRepository {
    fun observeRemindInfo(id: Long): Flow<ReminderInfo>
    suspend fun getReminderInfo(id: Long): ReminderInfo?
    suspend fun getRepeatPeriod(id: Long): RepeatPeriod?
    suspend fun getDateTime(id: Long): LocalDateTime?
    suspend fun saveNoteReminderDate(id: Long, date: LocalDateTime?)
    suspend fun saveNoteReminderRepeatPeriod(id: Long, period: RepeatPeriod)
    fun saveInitialReminderDate(id: Long)

    fun getInitialRepeatPeriod(): RepeatPeriod
    suspend fun restoreInitialReminder(id: Long)
}

class ReminderRepositoryImpl @Inject constructor(
    private val interestingIdeaDao: InterestingIdeaDao,
    private val localDataSource: LocalDataSource,
    @Dispatcher(RememberDispatchers.IO)
    override val coroutineContext: CoroutineDispatcher
) : ReminderRepository, CoroutineScope {

    override fun observeRemindInfo(id: Long): Flow<ReminderInfo> = interestingIdeaDao.observeById(id)
        .map { it.toReminderInfo() }
        .flowOn(coroutineContext)

    private fun InterestingIdeaEntity.toReminderInfo() = ReminderInfo(
        date = reminderDate,
        repeatPeriod = repeatPeriod,
        noteTitle = title
    )

    override suspend fun getReminderInfo(id: Long): ReminderInfo? = withContext(coroutineContext) {
        interestingIdeaDao.getById(id)?.toReminderInfo()
    }

    override suspend fun getRepeatPeriod(id: Long): RepeatPeriod? = withContext(coroutineContext) {
        interestingIdeaDao.getById(id)?.repeatPeriod
    }

    override suspend fun getDateTime(id: Long): LocalDateTime? = withContext(coroutineContext) {
        interestingIdeaDao.getById(id)?.reminderDate
    }

    override suspend fun saveNoteReminderDate(id: Long, date: LocalDateTime?) = withContext(coroutineContext) {
        interestingIdeaDao.updateReminderDate(ReminderUpdate(id = id, reminderDate = date, lastEdited = Clock.System.localDateTimeNow()))
    }

    override suspend fun saveNoteReminderRepeatPeriod(id: Long, period: RepeatPeriod) = withContext(coroutineContext) {
        interestingIdeaDao.updateRepeatPeriod(RepeatPeriodUpdate(id = id, period = period, lastEdited = Clock.System.localDateTimeNow()))
    }

    override fun saveInitialReminderDate(id: Long) {
        launch {
            localDataSource.initialDateTime = getDateTime(id)
            getRepeatPeriod(id)?.let { localDataSource.initialRepeatPeriod = it }
        }
    }

    override fun getInitialRepeatPeriod(): RepeatPeriod = localDataSource.initialRepeatPeriod

    override suspend fun restoreInitialReminder(id: Long) = withContext(coroutineContext) {
        saveNoteReminderDate(id, localDataSource.initialDateTime)
        saveNoteReminderRepeatPeriod(id, localDataSource.initialRepeatPeriod)
    }
}