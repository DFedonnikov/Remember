package com.gnest.remember.model;

import com.gnest.remember.model.db.data.Memo;

import java.util.Collection;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.realm.RealmResults;

public interface IListFragmentModel {
    Flowable<RealmResults<Memo>> getData();

    Memo getMemoById(int id);

    Observable<List<Memo>> deleteSelected(Collection<Integer> selectedIds);

    Observable<List<Memo>> moveBetweenRealms(Collection<Integer> ids);

    void revertArchived(Memo toRevert);

    void revertDeleteMemo(Memo toRevert);

    void swapMemos(int fromId, int fromPosition, int toId, int toPosition);

    void setMemoAlarmFalse(int id);

    void openDB();

    void closeDB();
}
