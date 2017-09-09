package com.gnest.remember.presenter;

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

    void deleteMemo(int memoId, int memoPosition, List<ClickableMemo> memos, boolean isAlarmSet);

    void deleteSelectedMemos(SparseArray<ClickableMemo> selectedMemos, List<ClickableMemo> memos);

    void share(SparseArray<ClickableMemo> selectedList);

    void proccessMemoSwap(int fromId, int fromPosition, int toId, int toPosition);

    void proccessMemoAlarmShutdown(ClickableMemo clickableMemo);

    void singleChoiceClick(ClickableMemo mMemo, int verticalOrientationCode);

    void pressBackButton(int verticalOrientationCode, int horizontalOrientationCode);
}
