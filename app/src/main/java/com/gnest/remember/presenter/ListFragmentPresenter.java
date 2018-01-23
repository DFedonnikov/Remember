package com.gnest.remember.presenter;

import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;

import com.gnest.remember.model.IListFragmentModel;
import com.gnest.remember.model.ListFragmentModelImpl;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.view.IListFragmentView;
import com.gnest.remember.view.adapters.MySelectableAdapter;
import com.gnest.remember.view.layoutmanagers.MyGridLayoutManager;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import io.realm.RealmResults;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

import static android.widget.GridLayout.HORIZONTAL;

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
    public void processDeleteSelectedMemos(Collection<Integer> selectedIds) {
        if (isViewAttached()) {
            PublishSubject<Boolean> subject = PublishSubject.create();
            Subscription removeSelectedSubscription = mModel.deleteSelected(selectedIds)
                    .zipWith(getView().showConfirmRemovePopup(subject, selectedIds.size()), (memo, cancel) -> new Pair<>(cancel, memo))
                    .doOnSubscribe(() -> {
                        if (isViewAttached()) {
                            getView().getAdapter().clearSelectedList();
                            shutDownActionMode();
                        }
                    })
                    .subscribe(cancelMemoPair -> {
                        if (isViewAttached()) {
                            for (Memo processed : cancelMemoPair.second) {
                                if (!cancelMemoPair.first) {
                                    removeAlarmNotification(processed.getId(), processed.isAlarmSet());
                                } else {
                                    mModel.revertDeleteMemo(processed);
                                    getView().getAdapter().notifyDataSetChanged();
                                }
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
                    .zipWith(getView().showConfirmArchiveActionPopup(subject, 1), (memo, cancel) -> new Pair<>(cancel, memo))
                    .doOnSubscribe(() -> {
                        if (isViewAttached()) {
                            getView().getAdapter().notifyItemRemoved(memoPosition);
                            getView().getAdapter().notifyItemRangeChanged(memoPosition, getView().getAdapter().getItemCount() > 1 ? 1 : 0);
                            shutDownActionMode();
                        }
                    })
                    .subscribe(cancelMemoPair -> {
                        if (isViewAttached()) {
                            for (Memo processed : cancelMemoPair.second) {
                                if (!cancelMemoPair.first) {
                                    removeAlarmNotification(processed.getId(), processed.isAlarmSet());
                                } else {
                                    mModel.revertArchived(processed);
                                    getView().getAdapter().notifyDataSetChanged();
                                }
                            }
                        }
                    });
            compositeSubscription.add(confirmDismissSubscription);
        }
    }

    @Override
    public void processArchiveActionOnSelected(Collection<Integer> selectedIds) {
        if (isViewAttached()) {
            PublishSubject<Boolean> subject = PublishSubject.create();
            Subscription confirmArchiveSubscription = mModel.moveBetweenRealms(selectedIds)
                    .zipWith(getView().showConfirmArchiveActionPopup(subject, selectedIds.size()), (memo, cancel) -> new Pair<>(cancel, memo))
                    .doOnSubscribe(() -> {
                        if (isViewAttached()) {
                            getView().getAdapter().clearSelectedList();
                            shutDownActionMode();
                        }
                    })
                    .subscribe(cancelMemoPair -> {
                        if (isViewAttached()) {
                            for (Memo processed : cancelMemoPair.second) {
                                if (!cancelMemoPair.first) {
                                    removeAlarmNotification(processed.getId(), processed.isAlarmSet());
                                } else {
                                    mModel.revertArchived(processed);
                                    getView().getAdapter().notifyDataSetChanged();
                                }
                            }
                        }
                    });
            compositeSubscription.add(confirmArchiveSubscription);
        }
    }

    @Override
    public void processShare(Collection<Integer> selectedIds) {
        if (selectedIds.size() == 1) {
            Memo memo = mModel.getMemoById(selectedIds.iterator().next());
            if (memo != null && isViewAttached()) {
                getView().shareMemoText(memo.getMemoText());
            }
        }
    }

    @Override
    public void processOpenFromNotification(long id) {
        if (isViewAttached()) {
            getView().getDataLoadedSubject()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .distinctUntilChanged(dataLoaded -> dataLoaded)
                    .zipWith(getComputingLayoutOrScrollingSubject().distinctUntilChanged(layoutCompleted -> layoutCompleted), Pair::new)
                    .map(subjectsCompletedPair -> mModel.getMemoById((int) id))
                    .subscribe(memo -> {
                        MyGridLayoutManager manager = getView().getLayoutManager();
                        MySelectableAdapter adapter = getView().getAdapter();
                        manager.setSpanCount(1);
                        adapter.expandItems();
                        manager.setOrientation(HORIZONTAL);
                        manager.scrollToPositionWithOffset(memo.getPosition(), 0);
                        mModel.setMemoAlarmFalse(memo.getId());
                        getView().closeNotification(memo.getId());
                    });
        }
    }

    private Observable<Boolean> getComputingLayoutOrScrollingSubject() {
        BehaviorSubject<Boolean> computingLayoutOrScrollingSubject = BehaviorSubject.create();
        Subscription subscription = Observable.timer(50, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (isViewAttached()) {
                        RecyclerView recyclerView = getView().getRecyclerView();
                        if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                                && !recyclerView.isComputingLayout()) {
                            computingLayoutOrScrollingSubject.onNext(true);
                            computingLayoutOrScrollingSubject.onCompleted();
                        }
                    }
                });
        compositeSubscription.add(subscription);
        return computingLayoutOrScrollingSubject;
    }

    @Override
    public void processMemoSwap(int fromId, int fromPosition, int toId, int toPosition) {
        mModel.swapMemos(fromId, fromPosition, toId, toPosition);
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
    public void processPressBackButton(int verticalOrientationCode, int horizontalOrientationCode, int spanCount) {
        if (isViewAttached()) {
            IListFragmentView view = getView();
            MyGridLayoutManager manager = view.getLayoutManager();
            if (manager.getOrientation() == horizontalOrientationCode) {
                manager.setOrientation(verticalOrientationCode);
                manager.setSpanCount(spanCount);
                manager.scrollToPosition(manager.getLastPosition());
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
