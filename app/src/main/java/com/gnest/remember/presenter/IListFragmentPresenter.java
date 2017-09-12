package com.gnest.remember.presenter;

import android.os.Bundle;
import android.util.SparseArray;

import com.gnest.remember.model.data.ClickableMemo;
import com.gnest.remember.view.IListFragmentView;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;

import java.util.List;

/**
 * Created by DFedonnikov on 08.09.2017.
 */

public interface IListFragmentPresenter extends MvpPresenter<IListFragmentView> {

    void loadData();

    void processDeleteMemo(int memoId, int memoPosition, List<ClickableMemo> memos, boolean isAlarmSet);

    void processDeleteSelectedMemos(SparseArray<ClickableMemo> selectedMemos, List<ClickableMemo> memos);

    void processShare(SparseArray<ClickableMemo> selectedList);

    void processMemoSwap(int fromId, int fromPosition, int toId, int toPosition);

    void processMemoAlarmShutdown(ClickableMemo clickableMemo);

    void processSingleChoiceClick(ClickableMemo mMemo, int verticalOrientationCode);

    void processPressBackButton(int verticalOrientationCode, int horizontalOrientationCode);

    void processReturnFromEditMode(int lastOrientation, int lastPosition, boolean isExpanded);
}
