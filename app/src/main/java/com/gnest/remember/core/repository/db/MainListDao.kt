package com.gnest.remember.core.repository.db

import com.gnest.remember.di.modules.DataModule.Companion.ARCHIVE_DB
import com.gnest.remember.di.modules.DataModule.Companion.MAIN_DB
import com.gnest.remember.model.db.data.Memo
import com.gnest.remember.model.db.data.MemoRealmFields
import io.reactivex.Flowable
import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmResults
import java.util.ArrayList
import javax.inject.Inject
import javax.inject.Named

class MainListDao @Inject constructor(
        @Named(MAIN_DB) private val primaryRealm: Realm,
        @Named(ARCHIVE_DB) private val secondaryRealm: Realm) {


    fun close() {
        primaryRealm.close()
        secondaryRealm.close()
    }

    fun getData(): RealmResults<Memo> {
        return primaryRealm.where(Memo::class.java)
                .findAllAsync()
                .sort(MemoRealmFields.POSITION)
    }

    fun getMemoById(id: Int): Memo? {
        return primaryRealm.where(Memo::class.java)
                .equalTo(MemoRealmFields.ID, id)
                .findFirst()
    }

    //TODO пересмотреть
    fun deleteSelected(selectedIds: Collection<Int>): Observable<List<Memo>> {
        val toReturnList = ArrayList<Memo>()
        for (id in selectedIds) {
            val toRemove = primaryRealm.where(Memo::class.java)
                    .equalTo(MemoRealmFields.ID, id)
                    .findFirst() ?: throw IllegalStateException("Cannot find memo with id $id")
// Creating new Memo object because after deletion toRemove will become invalid to operate on.
            val toReturn = Memo(toRemove)
            toReturnList.add(toReturn)
            //remove from primary and vice a versa if revert
            removeFromRealm(primaryRealm, toRemove)
        }

        validatePositions(primaryRealm)
        return Observable.just(toReturnList)
    }

    fun moveBetweenRealms(ids: Collection<Int>): Observable<List<Memo>> {
        val memos = ArrayList<Memo>()
        for (id in ids) {
            memos.add(moveBetween(primaryRealm, secondaryRealm, id))
        }
        validatePositions(primaryRealm)
        return Observable.just(memos)
    }

    fun revertArchived(toRevert: Memo) {
        moveBetween(secondaryRealm, primaryRealm, toRevert.id)
        validatePositions(secondaryRealm)
    }

    fun revertDeleteMemo(toRevert: Memo) {
        insertToRealm(primaryRealm, toRevert)
    }

    //Returning moved Memo so we can revert changes if user cancels it
    private fun moveBetween(realmFrom: Realm, realmTo: Realm, memoId: Int): Memo {
        val toMove = realmFrom.where(Memo::class.java)
                .equalTo(MemoRealmFields.ID, memoId)
                .findFirst() ?: throw IllegalStateException("Cannot find memo with id $memoId")
// Creating new Memo object because after deletion toMove will become invalid to operate on.
        val toReturn = Memo(toMove)
        //Add to secondary realm if delete and vice a versa if revert
        insertToRealm(realmTo, toMove)

        //remove from primary realm and vice a versa if revert
        removeFromRealm(realmFrom, toMove)

        return toReturn
    }

    private fun insertToRealm(realmTo: Realm, toInsert: Memo) {
        realmTo.executeTransaction { realm1 ->
            var position = 0

            val positionNumber = realm1.where(Memo::class.java)
                    .max(MemoRealmFields.POSITION)
            if (positionNumber != null) {
                position = positionNumber.toInt() + 1
            }
            val temp = Memo(toInsert.id, toInsert.memoText, position, toInsert.color, toInsert.alarmDate, toInsert.isAlarmSet, false, true)
            realm1.insertOrUpdate(temp)
        }
    }

    private fun removeFromRealm(realmFrom: Realm, toRemove: Memo) {
        realmFrom.executeTransaction { realm -> toRemove.deleteFromRealm() }
    }

    fun swapMemos(fromId: Int, fromPosition: Int, toId: Int, toPosition: Int) {
        primaryRealm.executeTransaction { realm1 ->
            val from = realm1.where(Memo::class.java)
                    .equalTo(MemoRealmFields.ID, fromId)
                    .findFirst()
            val to = realm1.where(Memo::class.java)
                    .equalTo(MemoRealmFields.ID, toId)
                    .findFirst()
            if (from != null && to != null) {
                from.position = toPosition
                to.position = fromPosition
                realm1.insertOrUpdate(from)
                realm1.insertOrUpdate(to)
            }
        }
    }

    fun setMemoAlarmFalse(id: Int) {
        primaryRealm.executeTransactionAsync { realm1 ->
            val memo = realm1.where(Memo::class.java)
                    .equalTo(MemoRealmFields.ID, id)
                    .findFirst()
            if (memo != null) {
                memo.setAlarm(false)
                realm1.insertOrUpdate(memo)
            }
        }
    }

    private fun validatePositions(validatedRealm: Realm) {
        validatedRealm.executeTransaction { realm ->
            val memos = realm.where(Memo::class.java)
                    .findAll()
                    .sort(MemoRealmFields.POSITION)
            for (i in memos.indices) {
                val memoToUpdate = memos[i]
                if (memoToUpdate != null) {
                    memoToUpdate.position = i
                    realm.insertOrUpdate(memoToUpdate)
                }
            }
        }
    }
}