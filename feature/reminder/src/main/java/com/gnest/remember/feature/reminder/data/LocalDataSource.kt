package com.gnest.remember.feature.reminder.data

import com.gnest.remember.core.note.RepeatPeriod
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

interface LocalDataSource {

    var initialDateTime: LocalDateTime?
    var initialRepeatPeriod: RepeatPeriod
}

class LocalDataSourceImpl @Inject constructor() : LocalDataSource {

    override var initialDateTime: LocalDateTime? = null
    override var initialRepeatPeriod: RepeatPeriod = RepeatPeriod.Once
}