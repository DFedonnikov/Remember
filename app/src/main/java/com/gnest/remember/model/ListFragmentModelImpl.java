package com.gnest.remember.model;

import android.support.v4.util.Pair;
import android.util.SparseArray;

import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.model.db.data.MemoRealmFields;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class ListFragmentModelImpl implements IListFragmentModel {

    private final PublishSubject<Boolean> dataDeletedSubject = PublishSubject.create();

    private Realm realm;

    public ListFragmentModelImpl() {
        openDB();
    }

    @Override
    public void openDB() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void closeDB() {
        realm.close();
    }

    @Override
    public Observable<RealmResults<Memo>> getData() {
        return realm.where(Memo.class)
                .findAllSortedAsync(MemoRealmFields.POSITION)
                .asObservable();
    }

    @Override
    public Observable<Pair<Boolean, List<Integer>>> deleteSelectedMemosFromDB(SparseArray<Pair<Integer, Boolean>> selectedIdAlarmSet, OrderedRealmCollection<Memo> memoss) {
        List<Integer> deletedIds = new ArrayList<>();

        realm.executeTransactionAsync(realm1 -> {
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
    public Observable<Boolean> deleteMemoFromDB(int memoId, int memoPosition, OrderedRealmCollection<Memo> memoss) {
        realm.executeTransactionAsync(realm1 -> {
            RealmResults<Memo> memos = realm1.where(Memo.class)
                    .findAllSorted(MemoRealmFields.POSITION);
            memos.deleteFromRealm(memoPosition);
            for (int i = memoPosition; i < memos.size(); i++) {
                Memo memoToUpdate = memos.get(i);
                memoToUpdate.setPosition(i);
                realm1.insertOrUpdate(memoToUpdate);
            }
        }, () -> dataDeletedSubject.onNext(true));

        return dataDeletedSubject
                .subscribeOn(Schedulers.computation())
                .distinctUntilChanged();
    }

    @Override
    public void swapMemos(int fromId, int fromPosition, int toId, int toPosition) {
        realm.executeTransaction(realm1 -> {
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
        realm.executeTransactionAsync(realm1 -> {
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
