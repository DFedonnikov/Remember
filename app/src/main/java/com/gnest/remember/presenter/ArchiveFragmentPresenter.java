package com.gnest.remember.presenter;

import com.gnest.remember.data.datasources.MainLocalDataSource;

public class ArchiveFragmentPresenter extends ListFragmentPresenter {

    public ArchiveFragmentPresenter(MainLocalDataSource source) {
       super(source);
    }

    @Override
    boolean isReturnedToMainScreen() {
        return false;
    }
}