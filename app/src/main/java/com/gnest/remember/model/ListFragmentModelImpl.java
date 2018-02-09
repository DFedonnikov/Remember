package com.gnest.remember.model;

import android.support.annotation.Nullable;

import com.gnest.remember.App;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.model.db.data.MemoRealmFields;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;

public class ListFragmentModelImpl implements IListFragmentModel {

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
    public Observable<List<Memo>> deleteSelected(Collection<Integer> selectedIds) {
        ArrayList<Memo> toReturnList = new ArrayList<>();
        for (Integer id : selectedIds) {
            Memo toRemove = primaryRealm.where(Memo.class)
                    .equalTo(MemoRealmFields.ID, id)
                    .findFirst();
            if (toRemove == null) {
                throw new IllegalStateException("Cannot find memo with id " + id);
            }
            // Creating new Memo object because after deletion toRemove will become invalid to operate on.
            Memo toReturn = new Memo(toRemove);
            toReturnList.add(toReturn);
            //remove from primary and vice a versa if revert
            removeFromRealm(primaryRealm, toRemove);
        }

        validatePositions(primaryRealm);
        return Observable.just(toReturnList);
    }

    @Override
    public Observable<List<Memo>> moveBetweenRealms(Collection<Integer> ids) {
        List<Memo> memos = new ArrayList<>();
        for (Integer id : ids) {
            memos.add(moveBetween(primaryRealm, secondaryRealm, id));
        }
        validatePositions(primaryRealm);
        return Observable.just(memos);
    }

    @Override
    public void revertArchived(Memo toRevert) {
        moveBetween(secondaryRealm, primaryRealm, toRevert.getId());
        validatePositions(secondaryRealm);
    }

    @Override
    public void revertDeleteMemo(Memo toRevert) {
        insertToRealm(primaryRealm, toRevert);
    }

    //Returning moved Memo so we can revert changes if user cancels it
    private Memo moveBetween(Realm realmFrom, Realm realmTo, int memoId) {
        Memo toMove = realmFrom.where(Memo.class)
                .equalTo(MemoRealmFields.ID, memoId)
                .findFirst();
        if (toMove == null) {
            throw new IllegalStateException("Cannot find memo with id " + memoId);
        }
        // Creating new Memo object because after deletion toMove will become invalid to operate on.
        Memo toReturn = new Memo(toMove);
        //Add to secondary realm if delete and vice a versa if revert
        insertToRealm(realmTo, toMove);

        //remove from primary realm and vice a versa if revert
        removeFromRealm(realmFrom, toMove);

        return toReturn;
    }

    void insertToRealm(Realm realmTo, Memo toInsert) {
        realmTo.executeTransaction(realm1 -> {
            int position = 0;

            Number positionNumber = realm1.where(Memo.class)
                    .max(MemoRealmFields.POSITION);
            if (positionNumber != null) {
                position = positionNumber.intValue() + 1;
            }
            Memo temp = new Memo(toInsert.getId(), toInsert.getMemoText(), position, toInsert.getColor(), toInsert.getAlarmDate(), toInsert.isAlarmSet(), false, true);
            realm1.insertOrUpdate(temp);
        });
    }

    private void removeFromRealm(Realm realmFrom, Memo toRemove) {
        realmFrom.executeTransaction(realm -> toRemove.deleteFromRealm());
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

    private void validatePositions(Realm validatedRealm) {
        validatedRealm.executeTransaction(realm -> {
            RealmResults<Memo> memos = realm.where(Memo.class)
                    .findAllSorted(MemoRealmFields.POSITION);
            for (int i = 0; i < memos.size(); i++) {
                Memo memoToUpdate = memos.get(i);
                memoToUpdate.setPosition(i);
                realm.insertOrUpdate(memoToUpdate);
            }
        });
    }
}
