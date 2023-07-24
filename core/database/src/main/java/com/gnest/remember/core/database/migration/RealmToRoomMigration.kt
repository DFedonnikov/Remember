package com.gnest.remember.core.database.migration

import com.gnest.remember.core.common.extensions.localDateTimeNow
import com.gnest.remember.core.common.network.Dispatcher
import com.gnest.remember.core.common.network.RememberDispatchers
import com.gnest.remember.core.database.dao.InterestingIdeaDao
import com.gnest.remember.core.database.di.ArchivedRealm
import com.gnest.remember.core.database.di.MainRealm
import com.gnest.remember.core.database.model.InterestingIdeaEntity
import com.gnest.remember.core.database.model.old.Memo
import com.gnest.remember.core.note.NoteColor
import com.gnest.remember.core.note.RepeatPeriod
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class RealmToRoomMigration @Inject constructor(
    private val interestingIdeaDao: InterestingIdeaDao,
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
            val memosList = mainRealm.where(Memo::class.java).findAll()
                .map { it.toRoomModel(isArchived = false) }
            val archivedMemosList = archivedRealm.where(Memo::class.java).findAll()
                .map { it.toRoomModel(isArchived = true) }
            interestingIdeaDao.insertAll(memosList)
            interestingIdeaDao.insertAll(archivedMemosList)
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

private fun Memo.toRoomModel(isArchived: Boolean): InterestingIdeaEntity = InterestingIdeaEntity(
    id = id.toLong(),
    title = "",
    text = memoText,
    position = position,
    color = when (color) {
        "YELLOW" -> NoteColor.CAPE_HONEY
        "BLUE" -> NoteColor.PICTON_BLUE
        "EMERALD" -> NoteColor.AERO_BLUE
        "PURPLE" -> NoteColor.WHITE_LILAC
        else -> NoteColor.WHITE
    },
    lastEdited = Clock.System.localDateTimeNow(),
    reminderDate = parseAlarmDate(),
    isReminderSet = isAlarmSet,
    isFinished = isArchived,
    repeatPeriod = RepeatPeriod.Once
)

private fun Memo.parseAlarmDate() = runCatching {
    alarmDate.takeIf { it > 0 }?.let {
        Instant.fromEpochMilliseconds(alarmDate).toLocalDateTime(TimeZone.currentSystemDefault())
    }
}.getOrNull()