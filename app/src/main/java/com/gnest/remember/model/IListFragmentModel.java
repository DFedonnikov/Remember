package com.gnest.remember.model;

import android.util.SparseArray;

import com.gnest.remember.model.data.ClickableMemo;

import java.util.List;

import rx.Observable;

/**
 * Created by DFedonnikov on 23.08.2017.
 */

public interface IListFragmentModel {
    Observable<?> getData();

    Observable<List<Integer>> deleteSelectedMemosFromDB(SparseArray<ClickableMemo> selectedMemos, List<ClickableMemo> memos);

    Observable<Boolean> deleteMemoFromDB(int memoId, int memoPosition, List<ClickableMemo> memos);

    Observable<Void> swapMemos(int fromId, int fromPosition, int toId, int toPosition);

    Observable<Void> setMemoAlarmFalse(int id);

    Observable<Void> updateExpandedColumn(boolean itemsExpanded);

    void openDB();

    void closeDB();
}
