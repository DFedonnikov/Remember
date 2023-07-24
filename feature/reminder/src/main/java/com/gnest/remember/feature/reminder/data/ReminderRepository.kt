package com.gnest.remember.feature.reminder.data

import com.gnest.remember.core.common.extensions.localDateTimeNow
import com.gnest.remember.core.common.network.Dispatcher
import com.gnest.remember.core.common.network.RememberDispatchers
import com.gnest.remember.core.database.dao.InterestingIdeaDao
import com.gnest.remember.core.database.model.ReminderUpdate
import com.gnest.remember.core.database.model.RepeatPeriodUpdate
import com.gnest.remember.core.note.RepeatPeriod
import com.gnest.remember.feature.reminder.domain.ReminderInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

interface ReminderRepository {
    fun observeRemindInfo(id: Long): Flow<ReminderInfo>
    fun saveNoteReminderDate(id: Long, date: LocalDateTime?)
    fun saveNoteReminderRepeatPeriod(id: Long, period: RepeatPeriod)
}

class ReminderRepositoryImpl @Inject constructor(
    private val interestingIdeaDao: InterestingIdeaDao,
    @Dispatcher(RememberDispatchers.IO)
    override val coroutineContext: CoroutineDispatcher
) : ReminderRepository, CoroutineScope {

    override fun observeRemindInfo(id: Long): Flow<ReminderInfo> = interestingIdeaDao.observeById(id)
        .map { ReminderInfo(date = it.reminderDate, repeatPeriod = it.repeatPeriod) }

    override fun saveNoteReminderDate(id: Long, date: LocalDateTime?) {
        launch {
            interestingIdeaDao.updateReminderDate(ReminderUpdate(id = id, reminderDate = date, lastEdited = Clock.System.localDateTimeNow()))
        }
    }

    override fun saveNoteReminderRepeatPeriod(id: Long, period: RepeatPeriod) {
        launch {
            interestingIdeaDao.updateRepeatPeriod(RepeatPeriodUpdate(id = id, period = period, lastEdited = Clock.System.localDateTimeNow()))
        }
    }
}