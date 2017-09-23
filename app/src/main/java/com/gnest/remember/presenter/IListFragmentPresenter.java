package com.gnest.remember.presenter;

import android.util.SparseArray;

import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.view.IListFragmentView;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;

import java.util.List;

import io.realm.OrderedRealmCollection;

/**
 * Created by DFedonnikov on 08.09.2017.
 */

public interface IListFragmentPresenter extends MvpPresenter<IListFragmentView> {

    void loadData();

    void processDeleteMemo(int memoId, int memoPosition, OrderedRealmCollection<Memo> memos, boolean isAlarmSet);

    void processDeleteSelectedMemos(SparseArray<Memo> selectedMemos, OrderedRealmCollection<Memo> memos);

    void processShare(SparseArray<Memo> selectedList);

    void processMemoSwap(int fromId, int fromPosition, int toId, int toPosition);

    void processMemoAlarmShutdown(Memo memo);

    void processSingleChoiceClick(Memo mMemo, int verticalOrientationCode);

    void processPressBackButton(int verticalOrientationCode, int horizontalOrientationCode);

    void processReturnFromEditMode(int lastOrientation, int lastPosition, boolean isExpanded);
}
