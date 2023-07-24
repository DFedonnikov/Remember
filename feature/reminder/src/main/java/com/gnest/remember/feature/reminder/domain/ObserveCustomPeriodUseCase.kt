package com.gnest.remember.feature.reminder.domain

import com.gnest.remember.core.note.RepeatPeriod
import com.gnest.remember.feature.reminder.data.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal interface ObserveCustomPeriodUseCase : (Long) -> Flow<RepeatPeriod.Custom>

internal class ObserveCustomPeriodUseCaseImpl @Inject constructor(private val repository: ReminderRepository) :
    ObserveCustomPeriodUseCase {

    override fun invoke(id: Long): Flow<RepeatPeriod.Custom> = repository.observeRemindInfo(id).map {
        (it.repeatPeriod as? RepeatPeriod.Custom) ?: RepeatPeriod.Custom(emptyList())
    }
}