package com.gnest.remember.presenter;

import android.support.v4.util.Pair;

import com.gnest.remember.model.IListFragmentModel;
import com.gnest.remember.model.ListFragmentModelImpl;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.view.IListFragmentView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.Collections;
import java.util.List;

import io.realm.RealmResults;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class ListFragmentPresenter extends MvpBasePresenter<IListFragmentView> implements IListFragmentPresenter {

    IListFragmentModel mModel;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    public ListFragmentPresenter() {
        mModel = new ListFragmentModelImpl();
    }

    @Override
    public void attachView(IListFragmentView view) {
        mModel.openDB();
        super.attachView(view);
    }

    @Override
    public void detachView(boolean retainInstance) {
        mModel.closeDB();
        compositeSubscription.unsubscribe();
        super.detachView(retainInstance);
    }

    @Override
    public void loadData() {
        Subscription getDataSubscription = mModel.getData()
                .observeOn(AndroidSchedulers.mainThread())
                .filter(RealmResults::isLoaded)
                .first()
                .subscribe(memos -> {
                    if (isViewAttached()) {
                        getView().setData(memos);
                    }
                });
        compositeSubscription.add(getDataSubscription);
    }

    @Override
    public void processReturnFromEditMode(int lastPosition, int lastOrientation, boolean isExpanded) {
        if (isViewAttached()) {
            IListFragmentView view = getView();
            if (view != null) {
                view.getLayoutManager().setAncorPos(lastPosition);
                view.getLayoutManager().setOrientation(lastOrientation);
                view.getAdapter().setItemsExpanded(isExpanded);
            }
        }
    }

    @Override
    public void processDeleteSelectedMemos(List<Integer> selectedIds) {
        if (isViewAttached()) {
            PublishSubject<Boolean> subject = PublishSubject.create();
            Subscription removeSelectedSubscription = mModel.deleteSelected(selectedIds)
                    .zipWith(getView().showConfirmRemovePopup(subject), (memo, cancel) -> new Pair<>(cancel, memo))
                    .doOnSubscribe(() -> {
                        if (isViewAttached()) {
                            getView().getAdapter().notifyDataSetChanged();
                            shutDownActionMode();
                        }
                    })
                    .subscribe(cancelMemoPair -> {
                        if (isViewAttached()) {
                            Memo processed = cancelMemoPair.second;
                            if (!cancelMemoPair.first) {
                                removeAlarmNotification(processed.getId(), processed.isAlarmSet());
                            } else {
                                mModel.revertDeleteMemo(processed);
                                getView().getAdapter().notifyDataSetChanged();
                            }
                        }
                    });
            compositeSubscription.add(removeSelectedSubscription);
        }
    }

    @Override
    public void processSwipeDismiss(int memoId, int memoPosition) {
        if (isViewAttached()) {
            PublishSubject<Boolean> subject = PublishSubject.create();
            Subscription confirmDismissSubscription = mModel.moveBetweenRealms(Collections.singletonList(memoId))
                    .zipWith(getView().showConfirmArchiveActionPopup(subject), (memo, cancel) -> new Pair<>(cancel, memo))
                    .doOnSubscribe(() -> {
                        if (isViewAttached()) {
                            getView().getAdapter().notifyItemRemoved(memoPosition);
                            getView().getAdapter().notifyItemRangeChanged(memoPosition, getView().getAdapter().getItemCount() > 1 ? 1 : 0);
                            shutDownActionMode();
                        }
                    })
                    .subscribe(cancelMemoPair -> {
                        if (isViewAttached()) {
                            Memo processed = cancelMemoPair.second;
                            if (!cancelMemoPair.first) {
                                removeAlarmNotification(processed.getId(), processed.isAlarmSet());
                            } else {
                                mModel.revertArchived(processed);
                                getView().getAdapter().notifyDataSetChanged();
                            }
                        }
                    });
            compositeSubscription.add(confirmDismissSubscription);
        }
    }

    @Override
    public void processArchiveActionOnSelected(List<Integer> selectedIds) {
        if (isViewAttached()) {
            PublishSubject<Boolean> subject = PublishSubject.create();
            Subscription confirmArchiveSubscription = mModel.moveBetweenRealms(selectedIds)
                    .zipWith(getView().showConfirmArchiveActionPopup(subject), (memo, cancel) -> new Pair<>(cancel, memo))
                    .doOnSubscribe(() -> {
                        if (isViewAttached()) {
                            getView().getAdapter().notifyDataSetChanged();
                            shutDownActionMode();
                        }
                    })
                    .subscribe(cancelMemoPair -> {
                        if (isViewAttached()) {
                            Memo processed = cancelMemoPair.second;
                            if (!cancelMemoPair.first) {
                                removeAlarmNotification(processed.getId(), processed.isAlarmSet());
                            } else {
                                mModel.revertArchived(processed);
                                getView().getAdapter().notifyDataSetChanged();
                            }
                        }
                    });
            compositeSubscription.add(confirmArchiveSubscription);
        }
    }

    @Override
    public void processShare(List<Integer> selectedIds) {
        if (selectedIds.size() == 1) {
            Memo memo = mModel.getMemoById(selectedIds.get(0));
            if (memo != null && isViewAttached()) {
                getView().shareMemoText(memo.getMemoText());
            }
        }
    }


    @Override
    public void processMemoSwap(int fromId, int fromPosition, int toId, int toPosition) {
        mModel.swapMemos(fromId, fromPosition, toId, toPosition);
    }

    @Override
    public void processMemoAlarmShutdown(Memo memo) {
        mModel.setMemoAlarmFalse(memo.getId());
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

    private void shutDownActionMode() {
        if (isViewAttached() && getView().getActionMode() != null) {
            getView().getActionMode().finish();
        }
    }

    private void removeAlarmNotification(int memoId, boolean isAlarmSet) {
        if (isViewAttached() && isAlarmSet) {
            getView().removeAlarm(memoId);
        }
    }
}
