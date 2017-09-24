package com.gnest.remember.presenter;

import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.gnest.remember.model.IListFragmentModel;
import com.gnest.remember.model.ListFragmentModelImpl;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.view.IListFragmentView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;


/**
 * Created by DFedonnikov on 23.08.2017.
 */

public class ListFragmentPresenter extends MvpBasePresenter<IListFragmentView> implements IListFragmentPresenter {

    private IListFragmentModel mModel;
    private List<Subscription> subscriptions;

    @Nullable
    private Subscription getDataSubscription;
    @Nullable
    private Subscription deleteMemoSubscription;
    @Nullable
    private Subscription deleteSelectedSubscription;
    @Nullable
    private Subscription swapSubscription;
    @Nullable
    private Subscription alarmShutdownSubscription;

    public ListFragmentPresenter() {
        mModel = new ListFragmentModelImpl();
        subscriptions = new ArrayList<>();
    }

    @Override
    public void attachView(IListFragmentView view) {
        mModel.openDB();
        super.attachView(view);
    }

    @Override
    public void detachView(boolean retainInstance) {
        mModel.closeDB();
        tryToUnsubscribe(subscriptions);
        super.detachView(retainInstance);
    }

    private void tryToUnsubscribe(List<Subscription> subscriptions) {
        for (Subscription subscription : subscriptions) {
            if (isSubscribed(subscription)) {
                subscription.unsubscribe();
            }
        }
    }

    private boolean isSubscribed(Subscription subscription) {
        return subscription != null && !subscription.isUnsubscribed();
    }

    @Override
    public void loadData() {
        getDataSubscription = mModel.getData()
                .observeOn(AndroidSchedulers.mainThread())
                .filter(RealmResults::isLoaded)
                .first()
                .subscribe(memos -> {
                    if (isViewAttached()) {
                        getView().setData(memos);
                    }
                });
        subscriptions.add(getDataSubscription);
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
    public void processDeleteSelectedMemos(SparseArray<Memo> selectedMemos, RealmResults<Memo> memos) {
        deleteSelectedSubscription = mModel.deleteSelectedMemosFromDB(selectedMemos, memos)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(deletedIds -> {
                    if (isViewAttached()) {
                        for (int i = 0; i < selectedMemos.size(); i++) {
                            Memo selected = selectedMemos.valueAt(i);
                            if (deletedIds.contains(selected.getId()) && selected.isAlarmSet()) {
                                getView().removeAlarm(selected.getId());
                            }
                            memos.remove(selected);
                        }
                        getView().getAdapter().notifyDataSetChanged();
                        getView().getActionMode().finish();
                    }
                });
        subscriptions.add(deleteSelectedSubscription);
    }

    @Override
    public void processDeleteMemo(int memoId, int memoPosition, RealmResults<Memo> memos, boolean isAlarmSet) {
        deleteMemoSubscription = mModel.deleteMemoFromDB(memoId, memoPosition, memos)
                .observeOn(AndroidSchedulers.mainThread())
                .take(2)
                .subscribe(isDeleted -> {
                    if (isDeleted) {
                        if (isViewAttached()) {
                            if (isAlarmSet) {
                                getView().removeAlarm(memoId);
                            }
//                        memos.remove(memoPosition);
                            getView().getAdapter().notifyItemRemoved(memoPosition);
                            getView().getAdapter().notifyItemRangeChanged(memoPosition, getView().getAdapter().getItemCount());
                        }
                        mModel.getDataDeletedSubject().onNext(false);
                    }
                });
        subscriptions.add(deleteMemoSubscription);
    }

    @Override
    public void processShare(SparseArray<Memo> selectedList) {
        if (selectedList.size() == 1 && isViewAttached()) {
            getView().shareMemoText(selectedList.valueAt(0).getMemoText());
        }
    }

    @Override
    public void processMemoSwap(int fromId, int fromPosition, int toId, int toPosition) {
        swapSubscription = mModel.swapMemos(fromId, fromPosition, toId, toPosition).subscribe();
        subscriptions.add(swapSubscription);
    }

    @Override
    public void processMemoAlarmShutdown(Memo memo) {
        alarmShutdownSubscription = mModel.setMemoAlarmFalse(memo.getId())
                .subscribe(aVoid -> memo.setAlarm(false));
        subscriptions.add(alarmShutdownSubscription);

    }

    @Override
    public void processSingleChoiceClick(Memo memo, int verticalOrientationCode) {
        if (isViewAttached()) {
            IListFragmentView view = getView();
            if (view.getLayoutManager().getOrientation() == verticalOrientationCode) {
                view.getLayoutManager().openItem(memo.getPosition());
            } else {
                view.getInteractionListener().onEnterEditMode(memo.getId());
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
            } else {
                view.getInteractionListener().onBackButtonPressed();
            }
        }
    }
}
