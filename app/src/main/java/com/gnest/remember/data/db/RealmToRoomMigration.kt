package com.gnest.remember.data.db

import com.gnest.remember.data.db.entity.Memo
import com.gnest.remember.data.db.entity.MemoColor
import com.gnest.remember.utils.DispatcherProvider
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.coroutines.CoroutineContext

typealias OldMemo = com.gnest.remember.model.db.data.Memo

class RealmToRoomMigration(private val memoDao: MemoDao,
                           private val mainRealmConfig: RealmConfiguration,
                           private val archivedRealmConfig: RealmConfiguration,
                           dispatchers: DispatcherProvider) : CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + dispatchers.single()

    private var mainRealm: Realm? = null
    private var archivedRealm: Realm? = null

    suspend fun migrateFromRealmToRoom() {
        withContext(coroutineContext) {
            openDb()
            val mainRealm = requireNotNull(mainRealm)
            val archivedRealm = requireNotNull(archivedRealm)

            val memosList = mainRealm.where(OldMemo::class.java).findAll().map { it.toRoomModel(isArchived = false) }
            val archivedMemosList = archivedRealm.where(OldMemo::class.java).findAll().map { it.toRoomModel(isArchived = true) }
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
    alarmDate.takeIf { it > 0 }?.let { Instant.fromEpochMilliseconds(alarmDate).toLocalDateTime(TimeZone.currentSystemDefault()) }
}.getOrNull()
