package com.gnest.remember.presenter;

import com.gnest.remember.model.ArchiveMemoModelImpl;

public class ArchiveFragmentPresenter extends ListFragmentPresenter {

    public ArchiveFragmentPresenter() {
        mModel = new ArchiveMemoModelImpl();
    }

}