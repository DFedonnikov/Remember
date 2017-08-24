package com.gnest.remember.presenter;

import com.gnest.remember.view.IView;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;

/**
 * Created by DFedonnikov on 23.08.2017.
 */

public interface IPresenter extends MvpPresenter<IView> {
    void loadData();
}
