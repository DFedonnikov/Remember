package com.gnest.remember.presenter;

import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.SparseArray;

import com.gnest.remember.model.IModel;
import com.gnest.remember.model.Model;
import com.gnest.remember.model.data.ClickableMemo;
import com.gnest.remember.view.IView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.List;

/**
 * Created by DFedonnikov on 23.08.2017.
 */

public class Presenter extends MvpBasePresenter<IView> implements IPresenter, Model.OnLoadDataListener {

    private IModel mModel;

    public Presenter(LoaderManager supportLoaderManager) {
        mModel = new Model(supportLoaderManager, this);
    }

    @Override
    public void loadData() {
        mModel.getData();

    }

    @Override
    public void dataLoaded(List<ClickableMemo> data) {
        if (isViewAttached()) {
            getView().setData(data);
        }
    }

    @Override
    public void deleteSelectedMemos(SparseArray<ClickableMemo> selectedMemos, List<ClickableMemo> memos) {
        for (int i = 0; i < selectedMemos.size(); i++) {
            ClickableMemo memo = selectedMemos.valueAt(i);
            deleteMemo(memo.getId(), memo.getPosition(), memos, memo.isAlarmSet());
        }
    }

    @Override
    public void deleteMemo(int memoId, int memoPosition, List<ClickableMemo> memos, boolean isAlarmSet) {
        mModel.deleteMemoFromDB(memoId, memoPosition, memos);
        if (isAlarmSet) {
            getView().removeAlarm(memoId);
        }
    }

    @Override
    public void share(SparseArray<ClickableMemo> selectedList) {
        if (selectedList.size() == 1 && isViewAttached()) {
            getView().shareMemoText(selectedList.valueAt(0).getMemoText());
        }
    }

    @Override
    public void proccessMemoSwap(int fromId, int fromPosition, int toId, int toPosition) {
        mModel.swapMemos(fromId, fromPosition, toId, toPosition);
    }

    @Override
    public void proccessMemoAlarmShutdown(ClickableMemo clickableMemo) {
        mModel.setMemoAlarmFalse(clickableMemo.getId());
        clickableMemo.setAlarmSet(false);
    }

    @Override
    public void singleChoiceClick(ClickableMemo memo, int verticalOrientationCode) {
        if (isViewAttached()) {
            IView view = getView();
            if (view.getLayoutManager().getOrientation() == verticalOrientationCode) {
                view.getLayoutManager().openItem(memo.getPosition());
            } else {
                view.getInteractionListener().onEnterEditMode(memo);
            }
        }
    }
}
