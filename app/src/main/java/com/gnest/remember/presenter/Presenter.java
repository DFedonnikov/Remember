package com.gnest.remember.presenter;

import android.support.v4.app.LoaderManager;

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
}
