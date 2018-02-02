package com.gnest.remember.presenter;

import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.view.IListFragmentView;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;

import java.util.Collection;

public interface IListFragmentPresenter extends MvpPresenter<IListFragmentView> {

    void loadData();

    void processDeleteSelectedMemos(Collection<Integer> selectedIds);

    void processShare(Collection<Integer> selectedIds);

    void processOpenFromNotification(long id);

    void processMemoSwap(int fromId, int fromPosition, int toId, int toPosition);

    void processSingleChoiceClick(Memo mMemo, int verticalOrientationCode);

    void processPressBackButton(int verticalOrientationCode, int horizontalOrientationCode, int spanCount);

    void processSwipeDismiss(int memoId, int memoPosition);

    void processArchiveActionOnSelected(Collection<Integer> selectedIds);
}
