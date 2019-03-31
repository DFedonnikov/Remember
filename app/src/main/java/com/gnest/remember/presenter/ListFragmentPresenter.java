package com.gnest.remember.presenter;

import androidx.core.util.Pair;

import com.gnest.remember.model.IListFragmentModel;
import com.gnest.remember.model.ListFragmentModelImpl;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.ui.view.IListFragmentView;
import com.gnest.remember.ui.adapters.MySelectableAdapter;
import com.gnest.remember.ui.layoutmanagers.MyGridLayoutManager;
import com.google.android.material.snackbar.Snackbar;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.jakewharton.rxbinding3.material.RxSnackbar;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.realm.RealmResults;

import static android.widget.GridLayout.HORIZONTAL;

public class ListFragmentPresenter extends MvpBasePresenter<IListFragmentView> implements IListFragmentPresenter {

    IListFragmentModel model;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ListFragmentPresenter() {
        model = new ListFragmentModelImpl();
    }

    @Override
    public void attachView(@Nonnull IListFragmentView view) {
        model.openDB();
        super.attachView(view);
    }

    @Override
    public void detachView() {
        model.closeDB();
        compositeDisposable.clear();
        super.detachView();
    }

    @Override
    public void destroy() {
        compositeDisposable.dispose();
        super.destroy();
    }

    @Override
    public void loadData() {
        Disposable disposable = model.getData()
                .observeOn(AndroidSchedulers.mainThread())
                .filter(RealmResults::isLoaded)
                .firstElement()
                .subscribe(memos -> ifViewAttached(view -> view.setData(memos)));
        compositeDisposable.add(disposable);
    }

    @Override
    public void processDeleteSelectedMemos(Collection<Integer> selectedIds) {
        performMemoTransaction(selectedIds, -1, TransactionStrategy.DELETE);
    }

    @Override
    public void processArchiveActionOnSelected(Collection<Integer> selectedIds) {
        performMemoTransaction(selectedIds, -1, TransactionStrategy.ARCHIVE);
    }

    @Override
    public void processSwipeDismiss(int memoId, int memoPosition) {
        performMemoTransaction(Collections.singletonList(memoId), memoPosition, TransactionStrategy.ARCHIVE);
    }

    private void performMemoTransaction(Collection<Integer> ids, int memoPosition, TransactionStrategy strategy) {
        ifViewAttached(view -> {
            Observable<Integer> snackbarObservable = getRxSnackbar(ids, view, strategy);
            Observable<List<Memo>> memoList = null;
            updateAlarmNotification(view, ids, strategy, isMovedToMainScreen());
            closeNotificationIfVisible(view, ids);
            switch (strategy) {
                case ARCHIVE:
                    memoList = model.moveBetweenRealms(ids);
                    break;
                case DELETE:
                    memoList = model.deleteSelected(ids);
                    break;
            }
            if (memoList == null) {
                return;
            }
            Disposable disposable = memoList
                    .zipWith(snackbarObservable, Pair::new)
                    .doOnSubscribe(disp -> {
                        MySelectableAdapter adapter = view.getAdapter();
                        if (ids.size() == 1 && memoPosition != -1) {
                            adapter.notifyItemRemoved(memoPosition);
                            adapter.notifyItemRangeChanged(memoPosition, view.getAdapter().getItemCount() > 1 ? 1 : 0);
                        } else {
                            adapter.clearSelectedList();
                        }
                        shutDownActionMode();
                    }).subscribe(listEventPair -> {
                        if (listEventPair.first != null && listEventPair.second != null) {
                            int event = listEventPair.second;
                            if (event == Snackbar.Callback.DISMISS_EVENT_ACTION) {
                                revertChanges(view, listEventPair.first, strategy);
                            }
                            if (strategy == TransactionStrategy.DELETE) {
                                removeFromCalendar(view, listEventPair.first);
                            }
                        }
                    });
            compositeDisposable.add(disposable);
        });
    }

    private void closeNotificationIfVisible(IListFragmentView view, Collection<Integer> ids) {
        for (Integer id : ids) {
            if (view.isNotificationVisible(id)) {
                view.closeNotification(id);
            }
        }
    }

    private void removeFromCalendar(IListFragmentView view, List<Memo> memoList) {
        for (Memo memo : memoList) {
            view.removeFromCalendar(memo.getId());
        }
    }

    @Nonnull
    private Observable<Integer> getRxSnackbar(Collection<Integer> ids, IListFragmentView view, TransactionStrategy strategy) {
        Snackbar snackbar = null;
        switch (strategy) {
            case ARCHIVE:
                snackbar = view.getArchiveSnackbar(ids.size());
                break;
            case DELETE:
                snackbar = view.getDeleteSnackbar(ids.size());
        }
        return RxSnackbar.dismisses(snackbar);
    }

    private void revertChanges(IListFragmentView view, List<Memo> memos, TransactionStrategy strategy) {
        if (memos != null) {
            for (Memo processed : memos) {
                switch (strategy) {
                    case ARCHIVE:
                        model.revertArchived(processed);
                        break;
                    case DELETE:
                        model.revertDeleteMemo(processed);
                        break;
                }
                if (processed.isAlarmSet()) {
                    updateAlarm(processed, isReturnedToMainScreen());
                }
                view.getAdapter().notifyDataSetChanged();
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
    public void processOpenFromNotification(int id) {
        ifViewAttached(view ->
        {
            Memo memo = model.getMemoById(id);
            MyGridLayoutManager manager = view.getLayoutManager();
            MySelectableAdapter adapter = view.getAdapter();
            manager.setSpanCount(1);
            adapter.expandItems();
            manager.setOrientation(HORIZONTAL);
            manager.scrollToPositionWithOffset(memo.getPosition(), 0);
            model.setMemoAlarmFalse(id);
            view.closeNotification(id);
        });
    }

    @Override
    public void processMemoSwap(int fromId, int fromPosition, int toId, int toPosition) {
        model.swapMemos(fromId, fromPosition, toId, toPosition);
    }

    private void shutDownActionMode() {
        ifViewAttached(view -> {
            if (view.getActionMode() != null)
                view.getActionMode().finish();
        });
    }

    private void updateAlarmNotification(IListFragmentView view, Collection<Integer> ids, TransactionStrategy strategy, boolean isMovedToMainScreen) {
        for (Integer id : ids) {
            Memo memo = model.getMemoById(id);
            if (memo.isAlarmSet()) {
                switch (strategy) {
                    case ARCHIVE:
                        updateAlarm(memo, isMovedToMainScreen);
                        break;
                    case DELETE:
                        view.removeAlarm(memo.getId());
                        break;
                }
            }
        }
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

    private enum TransactionStrategy {
        ARCHIVE,
        DELETE
    }

}