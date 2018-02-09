package com.gnest.remember.presenter;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;

import com.gnest.remember.model.IListFragmentModel;
import com.gnest.remember.model.ListFragmentModelImpl;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.view.IListFragmentView;
import com.gnest.remember.view.adapters.MySelectableAdapter;
import com.gnest.remember.view.layoutmanagers.MyGridLayoutManager;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

    IListFragmentModel model;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    public ListFragmentPresenter() {
        model = new ListFragmentModelImpl();
    }

    @Override
    public void attachView(@NonNull IListFragmentView view) {
        model.openDB();
        super.attachView(view);
    }

    @Override
    public void detachView() {
        model.closeDB();
        compositeSubscription.clear();
        super.detachView();
    }

    @Override
    public void destroy() {
        compositeSubscription.unsubscribe();
        super.destroy();
    }

    @Override
    public void loadData() {
        Subscription getDataSubscription = model.getData()
                .observeOn(AndroidSchedulers.mainThread())
                .filter(RealmResults::isLoaded)
                .first()
                .subscribe(memos -> ifViewAttached(view -> view.setData(memos)));
        compositeSubscription.add(getDataSubscription);
    }

    @Override
    public void processDeleteSelectedMemos(Collection<Integer> selectedIds) {
        ifViewAttached(view -> {
            updateAlarmNotification(selectedIds, true, isMovedToMainScreen());
            PublishSubject<Boolean> subject = PublishSubject.create();
            Subscription removeSelectedSubscription = model.deleteSelected(selectedIds)
                    .zipWith(view.showConfirmRemovePopup(subject, selectedIds.size()), (memo, cancel) -> new Pair<>(cancel, memo))
                    .doOnSubscribe(() -> {
                        view.getAdapter().clearSelectedList();
                        shutDownActionMode();
                    })
                    .subscribe(cancelMemoListPair -> revertChanges(view, cancelMemoListPair, true));
            compositeSubscription.add(removeSelectedSubscription);
        });
    }

    @Override
    public void processSwipeDismiss(int memoId, int memoPosition) {
        ifViewAttached(view -> {
            updateAlarmNotification(Collections.singletonList(memoId), false, isMovedToMainScreen());
            PublishSubject<Boolean> subject = PublishSubject.create();
            Subscription confirmDismissSubscription = model.moveBetweenRealms(Collections.singletonList(memoId))
                    .zipWith(view.showConfirmArchiveActionPopup(subject, 1), (memo, cancel) -> new Pair<>(cancel, memo))
                    .doOnSubscribe(() -> {
                        view.getAdapter().notifyItemRemoved(memoPosition);
                        view.getAdapter().notifyItemRangeChanged(memoPosition, view.getAdapter().getItemCount() > 1 ? 1 : 0);
                        shutDownActionMode();
                    })
                    .subscribe(cancelMemoListPair -> revertChanges(view, cancelMemoListPair, false));
            compositeSubscription.add(confirmDismissSubscription);
        });
    }

    @Override
    public void processArchiveActionOnSelected(Collection<Integer> selectedIds) {
        ifViewAttached(view -> {
            updateAlarmNotification(selectedIds, false, isMovedToMainScreen());
            PublishSubject<Boolean> subject = PublishSubject.create();
            Subscription confirmArchiveSubscription = model.moveBetweenRealms(selectedIds)
                    .zipWith(view.showConfirmArchiveActionPopup(subject, selectedIds.size()), (memo, cancel) -> new Pair<>(cancel, memo))
                    .doOnSubscribe(() -> {
                        view.getAdapter().clearSelectedList();
                        shutDownActionMode();
                    })
                    .subscribe(cancelMemoListPair -> revertChanges(view, cancelMemoListPair, false));
            compositeSubscription.add(confirmArchiveSubscription);
        });
    }

    private void revertChanges(IListFragmentView view, Pair<Boolean, List<Memo>> cancelMemoListPair, boolean isDeleted) {
        if (cancelMemoListPair.first != null && cancelMemoListPair.second != null) {
            boolean canceled = cancelMemoListPair.first;
            List<Memo> memos = cancelMemoListPair.second;
            for (Memo processed : memos) {
                if (canceled) {
                    if (isDeleted) {
                        model.revertDeleteMemo(processed);
                    } else {
                        model.revertArchived(processed);
                    }
                    if (processed.isAlarmSet()) {
                        updateAlarm(processed, isReturnedToMainScreen());
                    }
                    view.getAdapter().notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void processShare(Collection<Integer> selectedIds) {
        ifViewAttached(view -> {
            if (selectedIds.size() == 1) {
                Memo memo = model.getMemoById(selectedIds.iterator().next());
                if (memo != null) {
                    view.shareMemoText(memo.getMemoText());
                }
            }
        });
    }

    @Override
    public void processOpenFromNotification(long id) {
        ifViewAttached(view ->
                view.getDataLoadedSubject()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .distinctUntilChanged(dataLoaded -> dataLoaded)
                        .zipWith(getComputingLayoutOrScrollingSubject(view).distinctUntilChanged(layoutCompleted -> layoutCompleted), Pair::new)
                        .map(subjectsCompletedPair -> model.getMemoById((int) id))
                        .subscribe(memo -> {
                            MyGridLayoutManager manager = view.getLayoutManager();
                            MySelectableAdapter adapter = view.getAdapter();
                            manager.setSpanCount(1);
                            adapter.expandItems();
                            manager.setOrientation(HORIZONTAL);
                            manager.scrollToPositionWithOffset(memo.getPosition(), 0);
                            model.setMemoAlarmFalse(memo.getId());
                            view.closeNotification(memo.getId());
                        }));
    }

    private Observable<Boolean> getComputingLayoutOrScrollingSubject(IListFragmentView view) {
        BehaviorSubject<Boolean> computingLayoutOrScrollingSubject = BehaviorSubject.create();
        Subscription subscription = Observable.timer(50, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    RecyclerView recyclerView = view.getRecyclerView();
                    if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                            && !recyclerView.isComputingLayout()) {
                        computingLayoutOrScrollingSubject.onNext(true);
                        computingLayoutOrScrollingSubject.onCompleted();
                    }
                });
        compositeSubscription.add(subscription);
        return computingLayoutOrScrollingSubject;
    }

    @Override
    public void processMemoSwap(int fromId, int fromPosition, int toId, int toPosition) {
        model.swapMemos(fromId, fromPosition, toId, toPosition);
    }

    @Override
    public void processSingleChoiceClick(Memo memo, int verticalOrientationCode) {
        ifViewAttached(view -> {
            if (view.getLayoutManager().getOrientation() == verticalOrientationCode) {
                view.getLayoutManager().openItem(memo.getPosition());
            } else {
                view.getInteractionListener().onEnterEditMode(memo.getId());
            }
        });
    }

    @Override
    public void processPressBackButton(int verticalOrientationCode, int horizontalOrientationCode, int spanCount) {
        ifViewAttached(view -> {
            MyGridLayoutManager manager = view.getLayoutManager();
            if (manager.getOrientation() == horizontalOrientationCode) {
                manager.setOrientation(verticalOrientationCode);
                manager.setSpanCount(spanCount);
                manager.scrollToPosition(manager.getLastPosition());
                view.getAdapter().setItemsExpanded(false);
            } else {
                view.getInteractionListener().onBackButtonPressed();
            }
        });
    }

    private void shutDownActionMode() {
        ifViewAttached(view -> {
            if (view.getActionMode() != null)
                view.getActionMode().finish();
        });
    }

    private void updateAlarmNotification(Collection<Integer> selectedIds, boolean isRemove, boolean isMovedToMainScreen) {
        ifViewAttached(view -> {
            for (Integer id : selectedIds) {
                Memo alarmUpdated = model.getMemoById(id);
                if (alarmUpdated.isAlarmSet()) {
                    if (isRemove) {
                        view.removeAlarm(alarmUpdated.getId());
                    } else {
                        updateAlarm(alarmUpdated, isMovedToMainScreen);
                    }
                }
            }
        });
    }

    private void updateAlarm(Memo alarmUpdated, boolean isMovedToMainScreen) {
        ifViewAttached(view -> {
            String notificationTextLocal = alarmUpdated.getMemoText();
            if (notificationTextLocal != null && notificationTextLocal.length() > 10) {
                notificationTextLocal = notificationTextLocal.substring(0, 10).concat("...");
            }
            view.setAlarm(alarmUpdated.getId(), alarmUpdated.getAlarmDate(), notificationTextLocal, true, isMovedToMainScreen);
        });
    }

    private boolean isMovedToMainScreen() {
        return this instanceof ArchiveFragmentPresenter;
    }

    boolean isReturnedToMainScreen() {
        return true;
    }
}
