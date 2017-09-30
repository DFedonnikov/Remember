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
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class ListFragmentPresenter extends MvpBasePresenter<IListFragmentView> implements IListFragmentPresenter {

    private IListFragmentModel mModel;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Nullable
    private Subscription getDataSubscription;
    @Nullable
    private Subscription deleteMemoSubscription;
    @Nullable
    private Subscription deleteSelectedSubscription;

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
        getDataSubscription = mModel.getData()
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
                view.getLayoutManager().setmAncorPos(lastPosition);
                view.getLayoutManager().setOrientation(lastOrientation);
                view.getAdapter().setItemsExpanded(isExpanded);
            }
        }
    }

    @Override
    public void processDeleteSelectedMemos(SparseArray<Pair<Integer, Boolean>> selectedIdAlarmSet, RealmResults<Memo> memos) {
        deleteSelectedSubscription = mModel.deleteSelectedMemosFromDB(selectedIdAlarmSet, memos)
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
    public void processDeleteMemo(int memoId, int memoPosition, RealmResults<Memo> memos, boolean isAlarmSet) {
        deleteMemoSubscription = mModel.deleteMemoFromDB(memoId, memoPosition, memos)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isDeleted -> {
                    if (ListFragmentPresenter.this.isViewAttached()) {
                        if (isAlarmSet) {
                            getView().removeAlarm(memoId);
                        }
                        getView().getAdapter().notifyItemRemoved(memoPosition);
                        getView().getAdapter().notifyItemRangeChanged(memoPosition, getView().getAdapter().getItemCount());
                    }
                });
        compositeSubscription.add(deleteMemoSubscription);
    }

    @Override
    public void processShare(SparseArray<Pair<Integer, Boolean>> selectedIdAlarmSet) {
        if (selectedIdAlarmSet.size() == 1 && isViewAttached()) {
            Realm realm = null;
            try {
                realm = Realm.getDefaultInstance();
                Memo memo = realm.where(Memo.class)
                        .equalTo(MemoRealmFields.ID, selectedIdAlarmSet.valueAt(0).first)
                        .findFirst();
                if (memo != null) {
                    getView().shareMemoText(memo.getMemoText());
                }
            } finally {
                if (realm != null) {
                    realm.close();
                }
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
