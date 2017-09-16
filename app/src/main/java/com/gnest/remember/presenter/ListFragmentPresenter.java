package com.gnest.remember.presenter;

import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.gnest.remember.model.IListFragmentModel;
import com.gnest.remember.model.ListFragmentModelImpl;
import com.gnest.remember.model.data.ClickableMemo;
import com.gnest.remember.view.IListFragmentView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;


/**
 * Created by DFedonnikov on 23.08.2017.
 */

public class ListFragmentPresenter extends MvpBasePresenter<IListFragmentView> implements IListFragmentPresenter {

    private IListFragmentModel mModel;

    @Nullable
    private Subscription subscription;

    public ListFragmentPresenter() {
        mModel = new ListFragmentModelImpl();
    }

    @Override
    public void detachView(boolean retainInstance) {
        tryToUnsubscribe(subscription);
        super.detachView(retainInstance);
    }

    private void tryToUnsubscribe(Subscription subscription) {
        if (isSubscribed(subscription)) {
            subscription.unsubscribe();
        }
    }

    private boolean isSubscribed(Subscription subscription) {
        return subscription != null && !subscription.isUnsubscribed();
    }

    @Override
    public void loadData() {
        subscription = mModel.getData()
                .observeOn(AndroidSchedulers.mainThread())
                .filter(memos -> !memos.isEmpty())
                .subscribe(memos -> {
                    if (isViewAttached()) {
                        getView().setData(memos);
                    }
                });
    }

    @Override
    public void processReturnFromEditMode(int lastPosition, int lastOrientation, boolean isExpanded) {
        if (isViewAttached()) {
            IListFragmentView view = getView();
            if (view != null) {
                view.getLayoutManager().setmAncorPos(lastPosition);
                view.getLayoutManager().setOrientation(lastOrientation);
                view.getAdapter().setItemsExpanded(isExpanded);
            }
        }
    }

    @Override
    public void processDeleteSelectedMemos(SparseArray<ClickableMemo> selectedMemos, List<ClickableMemo> memos) {
        for (int i = 0; i < selectedMemos.size(); i++) {
            ClickableMemo memo = selectedMemos.valueAt(i);
            processDeleteMemo(memo.getId(), memo.getPosition(), memos, memo.isAlarmSet());
        }
    }

    @Override
    public void processDeleteMemo(int memoId, int memoPosition, List<ClickableMemo> memos, boolean isAlarmSet) {
        mModel.deleteMemoFromDB(memoId, memoPosition, memos);
        if (isAlarmSet) {
            getView().removeAlarm(memoId);
        }
    }

    @Override
    public void processShare(SparseArray<ClickableMemo> selectedList) {
        if (selectedList.size() == 1 && isViewAttached()) {
            getView().shareMemoText(selectedList.valueAt(0).getMemoText());
        }
    }

    @Override
    public void processMemoSwap(int fromId, int fromPosition, int toId, int toPosition) {
        mModel.swapMemos(fromId, fromPosition, toId, toPosition);
    }

    @Override
    public void processMemoAlarmShutdown(ClickableMemo clickableMemo) {
        mModel.setMemoAlarmFalse(clickableMemo.getId());
        clickableMemo.setAlarm(false);
    }

    @Override
    public void processSingleChoiceClick(ClickableMemo memo, int verticalOrientationCode) {
        if (isViewAttached()) {
            IListFragmentView view = getView();
            if (view.getLayoutManager().getOrientation() == verticalOrientationCode) {
                view.getLayoutManager().openItem(memo.getPosition());
            } else {
                view.getInteractionListener().onEnterEditMode(memo);
            }
        }
    }

    @Override
    public void processPressBackButton(int verticalOrientationCode, int horizontalOrientationCode) {
        if (isViewAttached()) {
            IListFragmentView view = getView();
            if (view.getLayoutManager().getOrientation() == horizontalOrientationCode) {
                view.getLayoutManager().setOrientation(verticalOrientationCode);
                view.getAdapter().setItemsExpanded(false);
                mModel.updateExpandedColumn(view.getAdapter().isItemsExpanded());
            } else {
                view.getInteractionListener().onBackButtonPressed();
            }
        }
    }
}
