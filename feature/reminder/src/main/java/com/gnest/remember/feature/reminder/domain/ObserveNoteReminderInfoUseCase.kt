package com.gnest.remember.feature.reminder.domain

import com.gnest.remember.feature.reminder.data.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal interface ObserveNoteReminderInfoUseCase : (Long) -> Flow<ReminderInfo>

internal class ObserveNoteReminderInfoUseCaseImpl @Inject constructor(private val repository: ReminderRepository) : ObserveNoteReminderInfoUseCase {

    override fun invoke(id: Long): Flow<ReminderInfo> = repository.observeRemindInfo(id)
}