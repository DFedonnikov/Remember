package com.gnest.remember.model;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.gnest.remember.App;
import com.gnest.remember.model.data.ClickableMemo;
import com.gnest.remember.model.db.DatabaseAccess;
import com.gnest.remember.model.loader.DBLoader;

import java.util.List;

/**
 * Created by DFedonnikov on 24.08.2017.
 */

public class Model implements IModel, LoaderManager.LoaderCallbacks<List<ClickableMemo>>  {

    private static final int LOADER_ID = 0;

    private LoaderManager mLoaderManager;
    private OnLoadDataListener mListener;
    private DatabaseAccess mDatabaseAccess;

    public Model(LoaderManager supportLoaderManager, OnLoadDataListener loadDataListener) {
        this.mLoaderManager = supportLoaderManager;
        this.mListener = loadDataListener;
        this.mDatabaseAccess = DatabaseAccess.getInstance(App.self());
    }

    @Override
    public void getData() {
        mDatabaseAccess.open();
        mLoaderManager.initLoader(LOADER_ID, null, this);
        mLoaderManager.getLoader(LOADER_ID).forceLoad();
    }

    @Override
    public Loader<List<ClickableMemo>> onCreateLoader(int id, Bundle args) {
        return new DBLoader(App.self(), mDatabaseAccess);
    }

    @Override
    public void onLoadFinished(Loader<List<ClickableMemo>> loader, List<ClickableMemo> data) {
        mDatabaseAccess.close();
        mListener.dataLoaded(data);
    }

    @Override
    public void onLoaderReset(Loader<List<ClickableMemo>> loader) {

    }

    public interface OnLoadDataListener {
        void dataLoaded(List<ClickableMemo> data);
    }
}
