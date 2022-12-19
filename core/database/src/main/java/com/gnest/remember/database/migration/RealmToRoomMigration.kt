package com.gnest.remember.database.migration

import com.gnest.remember.common.network.Dispatcher
import com.gnest.remember.common.network.RememberDispatchers
import com.gnest.remember.database.dao.MemoDao
import com.gnest.remember.database.di.ArchivedRealm
import com.gnest.remember.database.di.MainRealm
import com.gnest.remember.database.model.Memo
import com.gnest.remember.database.model.MemoColor
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

typealias OldMemo = com.gnest.remember.database.model.old.Memo

class RealmToRoomMigration @Inject constructor(
    private val memoDao: MemoDao,
    @MainRealm
    private val mainRealmConfig: RealmConfiguration,
    @ArchivedRealm
    private val archivedRealmConfig: RealmConfiguration,
    @Dispatcher(RememberDispatchers.SINGLE)
    dispatcher: CoroutineDispatcher
) : CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + dispatcher

    private var mainRealm: Realm? = null
    private var archivedRealm: Realm? = null

    suspend fun migrateFromRealmToRoom() {
        withContext(coroutineContext) {
            openDb()
            val mainRealm = requireNotNull(mainRealm)
            val archivedRealm = requireNotNull(archivedRealm)
            val memosList = mainRealm.where(OldMemo::class.java).findAll()
                .map { it.toRoomModel(isArchived = false) }
            val archivedMemosList = archivedRealm.where(OldMemo::class.java).findAll()
                .map { it.toRoomModel(isArchived = true) }
            memoDao.insertAll(memosList)
            memoDao.insertAll(archivedMemosList)
            closeDb()
        }
    }

    private fun openDb() {
        mainRealm = Realm.getInstance(mainRealmConfig)
        archivedRealm = Realm.getInstance(archivedRealmConfig)
    }

    private fun closeDb() {
        mainRealm?.close()
        archivedRealm?.close()
    }
}

private fun OldMemo.toRoomModel(isArchived: Boolean): Memo = Memo(
    id = id,
    text = memoText,
    position = position,
    color = runCatching { MemoColor.valueOf(color) }.getOrNull() ?: MemoColor.YELLOW,
    alarmDate = parseAlarmDate(),
    isAlarmSet = isAlarmSet,
    isArchived = isArchived
)

private fun OldMemo.parseAlarmDate() = runCatching {
    alarmDate.takeIf { it > 0 }?.let {
        Instant.fromEpochMilliseconds(alarmDate).toLocalDateTime(TimeZone.currentSystemDefault())
    }
}.getOrNull()
