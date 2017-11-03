package com.gnest.remember.presenter;

import android.support.v4.util.Pair;
import android.util.SparseArray;

import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.view.IListFragmentView;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;

import io.realm.RealmResults;

public interface IListFragmentPresenter extends MvpPresenter<IListFragmentView> {

    void loadData();

    void processDeleteMemo(int memoId, int memoPosition, RealmResults<Memo> memos, boolean isAlarmSet);

    void processDeleteSelectedMemos(SparseArray<Pair<Integer, Boolean>> selectedIdAlarmSet, RealmResults<Memo> memos);

    void processShare(SparseArray<Pair<Integer, Boolean>> selectedIdAlarmSet);

    void processMemoSwap(int fromId, int fromPosition, int toId, int toPosition);

    void processMemoAlarmShutdown(Memo memo);

    void processSingleChoiceClick(Memo mMemo, int verticalOrientationCode);

    void processPressBackButton(int verticalOrientationCode, int horizontalOrientationCode);

    void processReturnFromEditMode(int lastOrientation, int lastPosition, boolean isExpanded);

    void processSwipeDismiss(int memoId, int memoPosition, RealmResults<Memo> memos, boolean isAlarmSet);
}
