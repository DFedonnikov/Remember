package com.gnest.remember.presenter;

import com.gnest.remember.ui.view.IListFragmentView;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;

import java.util.Collection;

public interface IListFragmentPresenter extends MvpPresenter<IListFragmentView> {

    void loadData();

    void processDeleteSelectedMemos(Collection<Integer> selectedIds);

    void processShare(Collection<Integer> selectedIds);

    void processOpenFromNotification(int id);

    void processMemoSwap(int fromId, int fromPosition, int toId, int toPosition);

    void processSwipeDismiss(int memoId, int memoPosition);

    void processArchiveActionOnSelected(Collection<Integer> selectedIds);
}