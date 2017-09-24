package com.gnest.remember.model;

import android.support.v4.util.Pair;
import android.util.SparseArray;

import com.gnest.remember.model.db.data.Memo;

import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;
import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by DFedonnikov on 23.08.2017.
 */

public interface IListFragmentModel {
    Observable<RealmResults<Memo>> getData();

    Observable<List<Integer>> deleteSelectedMemosFromDB(SparseArray<Pair<Integer, Boolean>> selectedIdAlarmSet, OrderedRealmCollection<Memo> memos);

    Observable<Boolean> deleteMemoFromDB(int memoId, int memoPosition, OrderedRealmCollection<Memo> memos);

    Observable<Void> swapMemos(int fromId, int fromPosition, int toId, int toPosition);

    Observable<Void> setMemoAlarmFalse(int id);

    void openDB();

    void closeDB();

    BehaviorSubject<Boolean> getDataDeletedSubject();
}
