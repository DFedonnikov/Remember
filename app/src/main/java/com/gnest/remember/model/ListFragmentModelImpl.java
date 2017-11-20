package com.gnest.remember.model;

import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.SparseArray;

import com.gnest.remember.App;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.model.db.data.MemoRealmFields;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class ListFragmentModelImpl implements IListFragmentModel {

    private final PublishSubject<Boolean> dataDeletedSubject = PublishSubject.create();

    Realm primaryRealm;
    Realm secondaryRealm;

    public ListFragmentModelImpl() {
        openDB();
    }

    @Override
    public void openDB() {
        primaryRealm = Realm.getDefaultInstance();
        secondaryRealm = Realm.getInstance(App.getConfigurationByName(MemoRealmFields.ARCHIVE_CONFIG_NAME));
    }

    @Override
    public void closeDB() {
        primaryRealm.close();
        secondaryRealm.close();
    }

    @Override
    public Observable<RealmResults<Memo>> getData() {
        return primaryRealm.where(Memo.class)
                .findAllSortedAsync(MemoRealmFields.POSITION)
                .asObservable();
    }

    @Override
    @Nullable
    public Memo getMemoById(int id) {
        return primaryRealm.where(Memo.class)
                .equalTo(MemoRealmFields.ID, id)
                .findFirst();
    }

    @Override
    public Observable<Pair<Boolean, List<Integer>>> deleteSelectedMemosFromDB(SparseArray<Pair<Integer, Boolean>> selectedIdAlarmSet) {
        List<Integer> deletedIds = new ArrayList<>();

        primaryRealm.executeTransactionAsync(realm1 -> {
            RealmResults<Memo> memos = realm1.where(Memo.class)
                    .findAllSorted(MemoRealmFields.POSITION);
            for (int i = 0; i < selectedIdAlarmSet.size(); i++) {
                int id = selectedIdAlarmSet.valueAt(i).first;
                Memo memo = realm1
                        .where(Memo.class)
                        .equalTo(MemoRealmFields.ID, selectedIdAlarmSet.valueAt(i).first)
                        .findFirst();
                if (memo != null) {
                    int position = memo.getPosition();
                    deletedIds.add(id);
                    memos.deleteFromRealm(position);
                    for (int j = position; j < memos.size(); j++) {
                        Memo memoToUpdate = memos.get(j);
                        memoToUpdate.setPosition(j);
                        realm1.insertOrUpdate(memoToUpdate);
                    }
                }
            }
        }, () -> dataDeletedSubject.onNext(true));

        return dataDeletedSubject
                .subscribeOn(Schedulers.newThread())
                .distinctUntilChanged()
                .zipWith(Observable.just(deletedIds), Pair::new);
    }

    @Override
    public Observable<Memo> deleteMemo(int memoId) {
        return moveBetweenRealms(primaryRealm, secondaryRealm, memoId);
    }

    @Override
    public void revertDeleteMemo(Memo toRevert) {
        moveBetweenRealms(secondaryRealm, primaryRealm, toRevert.getId());
    }

    private Observable<Memo> moveBetweenRealms(Realm realmFrom, Realm realmTo, int memoId) {
        Memo toMove = realmFrom.where(Memo.class)
                .equalTo(MemoRealmFields.ID, memoId)
                .findFirst();
        if (toMove == null) {
            throw new IllegalStateException("Cannot find memo with id " + memoId);
        }
        // Creating new Memo object because after deletion toMove will become invalid to operate on.
        Memo toReturn = new Memo(toMove.getId(), toMove.getMemoText(), toMove.getPosition(), toMove.getColor(), toMove.getAlarmDate(), toMove.isAlarmSet());
            //Add to secondary realm if delete and vice a versa if revert
            insertToRealm(realmTo, toMove);

            //remove from primary and adjust positions if delete and vice a versa if revert
            removeFromRealm(realmFrom, toMove);

        return Observable.just(toReturn);
    }

    void insertToRealm(Realm realmTo, Memo toInsert) {
        realmTo.executeTransaction(realm1 -> {
            int position = 0;

            Number positionNumber = realm1.where(Memo.class)
                    .max(MemoRealmFields.POSITION);
            if (positionNumber != null) {
                position = positionNumber.intValue() + 1;
            }
            Memo temp = new Memo(toInsert.getId(), toInsert.getMemoText(), position, toInsert.getColor(), -1, false, false, true);
            realm1.insertOrUpdate(temp);
        });
    }

    void removeFromRealm(Realm realmFrom, Memo toRemove) {
        realmFrom.executeTransaction(realm -> {
            RealmResults<Memo> memos = realm.where(Memo.class)
                    .findAllSorted(MemoRealmFields.POSITION);
            int position = toRemove.getPosition();
            memos.deleteFromRealm(position);
            for (int i = position; i < memos.size(); i++) {
                Memo memoToUpdate = memos.get(i);
                memoToUpdate.setPosition(i);
                realm.insertOrUpdate(memoToUpdate);
            }
        });
    }

    @Override
    public void swapMemos(int fromId, int fromPosition, int toId, int toPosition) {
        primaryRealm.executeTransaction(realm1 -> {
            Memo from = realm1.where(Memo.class)
                    .equalTo(MemoRealmFields.ID, fromId)
                    .findFirst();
            Memo to = realm1.where(Memo.class)
                    .equalTo(MemoRealmFields.ID, toId)
                    .findFirst();
            if (from != null && to != null) {
                from.setPosition(toPosition);
                to.setPosition(fromPosition);
                realm1.insertOrUpdate(from);
                realm1.insertOrUpdate(to);
            }
        });
    }

    @Override
    public void setMemoAlarmFalse(int id) {
        primaryRealm.executeTransactionAsync(realm1 -> {
            Memo memo = realm1.where(Memo.class)
                    .equalTo(MemoRealmFields.ID, id)
                    .findFirst();
            if (memo != null) {
                memo.setAlarm(false);
                realm1.insertOrUpdate(memo);
            }
        });
    }
}
