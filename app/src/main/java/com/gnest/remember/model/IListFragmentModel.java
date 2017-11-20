package com.gnest.remember.model;

import android.support.v4.util.Pair;
import android.util.SparseArray;

import com.gnest.remember.model.db.data.Memo;

import java.util.List;

import io.realm.RealmResults;
import rx.Observable;

public interface IListFragmentModel {
    Observable<RealmResults<Memo>> getData();

    Memo getMemoById(int id);

    Observable<Pair<Boolean, List<Integer>>> deleteSelectedMemosFromDB(SparseArray<Pair<Integer, Boolean>> selectedIdAlarmSet);

    Observable<Memo> deleteMemo(int memoId);

    void revertDeleteMemo(Memo toRevert);

    void swapMemos(int fromId, int fromPosition, int toId, int toPosition);

    void setMemoAlarmFalse(int id);

    void openDB();

    void closeDB();
}
