package com.gnest.remember.presenter;

import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.SparseArray;

import com.gnest.remember.model.IListFragmentModel;
import com.gnest.remember.model.ListFragmentModelImpl;
import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.model.db.data.MemoRealmFields;
import com.gnest.remember.view.IListFragmentView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
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
    public void processDeleteSelectedMemos(SparseArray<Pair<Integer, Boolean>> selectedIdAlarmSet) {
        Subscription deleteSelectedSubscription = mModel.deleteSelectedMemosFromDB(selectedIdAlarmSet)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(deletedIdsPair -> {
                    if (isViewAttached()) {
                        for (int i = 0; i < selectedIdAlarmSet.size(); i++) {
                            Pair<Integer, Boolean> idAlarmPair = selectedIdAlarmSet.valueAt(i);
                            int id = idAlarmPair.first;
                            if (deletedIdsPair.second.contains(id) && idAlarmPair.second) {
                                getView().removeAlarm(id);
                            }
                        }
                        getView().getAdapter().notifyDataSetChanged();
                        getView().getActionMode().finish();
                    }
                });
        compositeSubscription.add(deleteSelectedSubscription);
    }

    @Override
    public void processSwipeDismiss(int memoId, int memoPosition, boolean isAlarmSet) {
        if (isViewAttached()) {
            PublishSubject<Boolean> subject = PublishSubject.create();
            Subscription confirmDismissSubscription = deleteMemoFromDb(memoId)
                    .zipWith(getView().showConfirmPopup(memoPosition, subject), (memo, cancel) -> new Pair<>(cancel, memo))
                    .doOnSubscribe(() -> {
                        getView().getAdapter().notifyItemRemoved(memoPosition);
                        getView().getAdapter().notifyItemRangeChanged(memoPosition, getView().getAdapter().getItemCount());
                    })
                    .subscribe(cancelMemoPair -> {
                        if (!cancelMemoPair.first) {
                            if (isAlarmSet) {
                                getView().removeAlarm(memoId);
                            }
                            if (getView().getActionMode() != null) {
                                getView().getActionMode().finish();
                            }
                        } else {
                            revertDeleteMemoFromDb(cancelMemoPair.second);
                            getView().getAdapter().notifyDataSetChanged();
                        }
                    });
            compositeSubscription.add(confirmDismissSubscription);
        }
    }

    private Observable<Memo> deleteMemoFromDb(int memoId) {
        return mModel.deleteMemo(memoId);
    }


    private void revertDeleteMemoFromDb(Memo toRevert) {
        mModel.revertDeleteMemo(toRevert);
    }

    @Override
    public void processShare(SparseArray<Pair<Integer, Boolean>> selectedIdAlarmSet) {
        if (selectedIdAlarmSet.size() == 1) {
            Memo memo = mModel.getMemoById(selectedIdAlarmSet.valueAt(0).first);
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
}
