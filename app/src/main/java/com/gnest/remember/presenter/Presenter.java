package com.gnest.remember.presenter;

import com.gnest.remember.model.IModel;
import com.gnest.remember.view.IView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

/**
 * Created by DFedonnikov on 23.08.2017.
 */

public class Presenter extends MvpBasePresenter<IView> implements IPresenter {

    private IView mView;
    private IModel mModel;

    public Presenter(IView view) {
        this.mView = view;
    }
}
