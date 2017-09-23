package com.gnest.remember.model;

import android.util.SparseArray;

import com.gnest.remember.model.db.DatabaseAccess;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.model.db.data.MemoRealmFields;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.schedulers.Schedulers;


/**
 * Created by DFedonnikov on 24.08.2017.
 */

public class ListFragmentModelImpl implements IListFragmentModel  {

    private DatabaseAccess mDatabaseAccess;
    private Realm realm;

    public ListFragmentModelImpl() {
//        this.mDatabaseAccess = DatabaseAccess.getInstance(App.self());
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

    private <T> Observable<T> getObservableFromCallable(Callable<T> callable) {
        return Observable
                .fromCallable(callable)
                .subscribeOn(Schedulers.computation())
                .retry((integer, throwable) -> {
                    if (throwable instanceof IllegalStateException) {
                        openDB();
                        return true;
                    } else {
                        return false;
                    }
                });
    }


    @Override
    public Observable<RealmResults<Memo>> getData() {
//        return getObservableFromCallable(mDatabaseAccess.getAllMemos());

        return realm.where(Memo.class)
                .findAllSortedAsync(MemoRealmFields.ID)
                .asObservable();
    }

    @Override
    public Observable<List<Integer>> deleteSelectedMemosFromDB(SparseArray<Memo> selectedMemos, OrderedRealmCollection<Memo> memos) {
//        return getObservableFromCallable(mDatabaseAccess.deleteSelected(selectedMemos, memos));
        List<Integer> deletedIds = new ArrayList<>();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i = 0; i < selectedMemos.size(); i++) {
                    Memo memo = selectedMemos.valueAt(i);
                    realm.where(Memo.class)
                            .equalTo(MemoRealmFields.ID, memo.getId())
                            .findFirst()
                            .deleteFromRealm();
                    deletedIds.add(memo.getId());
                    for (int j = memo.getPosition() + 1; j < memos.size(); j++) {
                        Memo memoToUpdate = memos.get(j);
                        realm.where(Memo.class)
                                .equalTo(MemoRealmFields.ID, memoToUpdate.getId())
                                .findFirst()
                                .setPosition(j - 1);
                    }
                }
            }
        });

        return Observable.just(deletedIds);
    }

    @Override
    public Observable<Void> deleteMemoFromDB(int memoId, int memoPosition, OrderedRealmCollection<Memo> memos) {
//        return getObservableFromCallable(mDatabaseAccess.delete(memoId, memoPosition, memos));

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Memo.class)
                        .equalTo(MemoRealmFields.ID, memoId)
                        .findFirst()
                        .deleteFromRealm();
                for (int i = memoPosition + 1; i < memos.size(); i++) {
                    Memo memoToUpdate = memos.get(i);
                    realm.where(Memo.class)
                            .equalTo(MemoRealmFields.ID, memoToUpdate.getId())
                            .findFirst()
                            .setPosition(i - 1);
                }
            }
        });

        return Observable.empty();
    }

    @Override
    public Observable<Void> swapMemos(int fromId, int fromPosition, int toId, int toPosition) {
//        return getObservableFromCallable(mDatabaseAccess.swapMemos(fromId, fromPosition, toId, toPosition));

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Memo.class)
                        .equalTo(MemoRealmFields.ID, fromId)
                        .findFirst()
                        .setPosition(toPosition);
                realm.where(Memo.class)
                        .equalTo(MemoRealmFields.ID, toId)
                        .findFirst()
                        .setPosition(fromPosition);
            }
        });

        return Observable.empty();
    }

    @Override
    public Observable<Void> setMemoAlarmFalse(int id) {
//       return getObservableFromCallable(mDatabaseAccess.setMemoAlarmFalse(id));

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Memo.class)
                        .equalTo(MemoRealmFields.ID, id)
                        .findFirst()
                        .setAlarm(false);

            }
        });
        return Observable.empty();
    }
}
