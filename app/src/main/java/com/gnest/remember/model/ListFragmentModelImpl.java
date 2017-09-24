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
import rx.subjects.BehaviorSubject;

/**
 * Created by DFedonnikov on 24.08.2017.
 */

public class ListFragmentModelImpl implements IListFragmentModel {

    private final BehaviorSubject<Boolean> dataDeletedSubject = BehaviorSubject.create();

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
    public Observable<List<Integer>> deleteSelectedMemosFromDB(SparseArray<Pair<Integer, Boolean>> selectedIdAlarmSet, OrderedRealmCollection<Memo> memoss) {
        List<Integer> deletedIds = new ArrayList<>();

        realm.executeTransactionAsync(realm1 -> {
            RealmResults<Memo> memos = realm1.where(Memo.class)
                    .findAllSorted(MemoRealmFields.POSITION);
            for (int i = 0; i < selectedIdAlarmSet.size(); i++) {
                int id = selectedIdAlarmSet.valueAt(i).first;
                int position = realm1
                        .where(Memo.class)
                        .equalTo(MemoRealmFields.ID, selectedIdAlarmSet.valueAt(i).first)
                        .findFirst()
                        .getPosition();
                deletedIds.add(id);
                memos.deleteFromRealm(position);
                for (int j = position; j < memos.size(); j++) {
                    Memo memoToUpdate = memos.get(j);
                    memoToUpdate.setPosition(j);
                    realm1.insertOrUpdate(memoToUpdate);
                }
            }
        }, () -> dataDeletedSubject.onNext(true));

        return dataDeletedSubject
                .subscribeOn(Schedulers.computation())
                .distinctUntilChanged()
                .zipWith(Observable.just(deletedIds), (deleted, integers) -> integers);
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
    public Observable<Void> swapMemos(int fromId, int fromPosition, int toId, int toPosition) {
        realm.executeTransaction(realm1 -> {
            Memo from = realm1.where(Memo.class)
                    .equalTo(MemoRealmFields.ID, fromId)
                    .findFirst();
            from.setPosition(toPosition);
            Memo to = realm1.where(Memo.class)
                    .equalTo(MemoRealmFields.ID, toId)
                    .findFirst();
            to.setPosition(fromPosition);
            realm1.insertOrUpdate(from);
            realm1.insertOrUpdate(to);
        });
        return Observable.empty();
    }

    @Override
    public Observable<Void> setMemoAlarmFalse(int id) {
        realm.executeTransactionAsync(realm1 -> realm1.where(Memo.class)
                .equalTo(MemoRealmFields.ID, id)
                .findFirst()
                .setAlarm(false));
        return Observable.empty();
    }

    @Override
    public BehaviorSubject<Boolean> getDataDeletedSubject() {
        return dataDeletedSubject;
    }
}
