package com.gnest.remember.feature.home.data

import com.gnest.remember.common.network.Dispatcher
import com.gnest.remember.common.network.RememberDispatchers
import com.gnest.remember.database.dao.InterestingIdeaDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface HomeRepository {
    fun hasAnyNotes(): Flow<Boolean>
}

class HomeRepositoryImpl @Inject constructor(
    private val interestingIdeaDao: InterestingIdeaDao,
    @Dispatcher(RememberDispatchers.IO)
    override val coroutineContext: CoroutineDispatcher
) : HomeRepository, CoroutineScope {

    override fun hasAnyNotes(): Flow<Boolean> = interestingIdeaDao.countNotes()
        .map { it > 0 }
        .flowOn(coroutineContext)
}