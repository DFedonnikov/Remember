package com.gnest.remember.model;

import android.util.SparseArray;

import com.gnest.remember.model.data.ClickableMemo;

import java.util.List;

import rx.Observable;

/**
 * Created by DFedonnikov on 23.08.2017.
 */

public interface IListFragmentModel {
    Observable<List<ClickableMemo>> getData();

    Observable<List<Integer>> deleteSelectedMemosFromDB(SparseArray<ClickableMemo> selectedMemos, List<ClickableMemo> memos);

    Observable<Boolean> deleteMemoFromDB(int memoId, int memoPosition, List<ClickableMemo> memos);

    void swapMemos(int fromId, int fromPosition, int toId, int toPosition);

    void setMemoAlarmFalse(int id);

    void updateExpandedColumn(boolean itemsExpanded);
}
