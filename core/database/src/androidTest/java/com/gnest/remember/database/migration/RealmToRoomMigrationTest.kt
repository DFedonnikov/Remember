package com.gnest.remember.database.migration

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.gnest.remember.database.dao.MemoDao
import com.gnest.remember.database.di.ArchivedRealm
import com.gnest.remember.database.di.MainRealm
import com.gnest.remember.database.model.Memo
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.realm.Realm
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

typealias OldMemo = com.gnest.remember.database.model.old.Memo

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class RealmToRoomMigrationTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dao: MemoDao
    @Inject
    @MainRealm
    lateinit var mainRealm: Realm
    @Inject
    @ArchivedRealm
    lateinit var archivedRealm: Realm
    @Inject
    lateinit var migration: RealmToRoomMigration

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        Realm.init(context)
        hiltRule.inject()
    }

    @After
    fun closeDb() {
        mainRealm.close()
        archivedRealm.close()
    }

    @Test
    @Throws(Exception::class)
    fun testMigrateDb() {
        runTest {
            val mainMemo1 = OldMemo(0, "Memo 1", 1, "BLUE", -1, false)
            val alarmDate = System.currentTimeMillis()
            val mainMemo2 = OldMemo(1, "Memo 2", 1, "EMERALD", alarmDate, true)
            val archivedMemo3 = OldMemo(2, "Memo 3", 0, "PURPLE", -1, false)
            val archivedMemo4 = OldMemo(3, "Memo 4", 1, "YELLOW", -1, false)

            mainRealm.executeTransaction {
                it.insert(mainMemo1)
                it.insert(mainMemo2)
            }
            archivedRealm.executeTransaction {
                it.insert(archivedMemo3)
                it.insert(archivedMemo4)
            }
            migration.migrateFromRealmToRoom()

            val memos = dao.getAll()
            val newMemo1 = memos.first { it.text == "Memo 1" }
            val newMemo2 = memos.first { it.text == "Memo 2" }
            val newMemo3 = memos.first { it.text == "Memo 3" }
            val newMemo4 = memos.first { it.text == "Memo 4" }
            newMemo1.assertEquals(mainMemo1, false)
            newMemo2.assertEquals(mainMemo2, false)
            newMemo3.assertEquals(archivedMemo3, true)
            newMemo4.assertEquals(archivedMemo4, true)
        }
    }

    private fun Memo.assertEquals(oldMemo: OldMemo, isArchived: Boolean) {
        assert(id == oldMemo.id) { "new memo: $this; old memo: $oldMemo" }
        assert(position == oldMemo.position)
        assert(color.name == oldMemo.color)
        if (oldMemo.alarmDate != -1L) {
            val newMemoMillis = alarmDate?.toInstant(TimeZone.currentSystemDefault())?.toEpochMilliseconds()
            assert(newMemoMillis == oldMemo.alarmDate) {
                "new memo alarmDate: $alarmDate; millis: $newMemoMillis old memo alarmDate millis: ${oldMemo.alarmDate}"
            }
        } else {
            assert(alarmDate == null)
        }
        assert(isAlarmSet == oldMemo.isAlarmSet)
        assert(this.isArchived == isArchived)
    }

    @Test
    @Throws(Exception::class)
    fun testBigMemoListMigration() {
        runTest {
            val mainMemos = (0..9999).map { OldMemo(it, "Memo $it", it, "BLUE", -1, false)}
            val archivedMemos = (10000..20000).map { OldMemo(it, "Memo $it", it, "EMERALD", -1, false)}

            mainRealm.executeTransaction {
                it.insert(mainMemos)
            }
            archivedRealm.executeTransaction {
                it.insert(archivedMemos)
            }
            migration.migrateFromRealmToRoom()

            val allMemos = dao.getAll()
            val mainNewMemos = allMemos.filter { !it.isArchived }
            val archiveNewMemos = allMemos.filter { it.isArchived }
            assert(mainMemos.size == mainNewMemos.size)
            assert(archivedMemos.size == archiveNewMemos.size)
            mainNewMemos.forEachIndexed { index, memo ->
                memo.assertEquals(mainMemos[index], isArchived = false)
            }
            archiveNewMemos.forEachIndexed { index, memo ->
                memo.assertEquals(archivedMemos[index], isArchived = true)
            }
        }
    }
}