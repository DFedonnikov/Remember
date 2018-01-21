package com.gnest.remember.model;

import com.gnest.remember.model.db.data.Memo;

import java.util.Collection;

import io.realm.RealmResults;
import rx.Observable;

public interface IListFragmentModel {
    Observable<RealmResults<Memo>> getData();

    Memo getMemoById(int id);

    Observable<Memo> deleteSelected(Collection<Integer> selectedIds);

    Observable<Memo> moveBetweenRealms(Collection<Integer> ids);

    void revertArchived(Memo toRevert);

    void revertDeleteMemo(Memo toRevert);

    void swapMemos(int fromId, int fromPosition, int toId, int toPosition);

    void setMemoAlarmFalse(int id);

    void openDB();

    void closeDB();
}
