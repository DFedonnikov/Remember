package com.gnest.remember.model;

import android.util.SparseArray;

import com.gnest.remember.App;
import com.gnest.remember.model.data.ClickableMemo;
import com.gnest.remember.model.db.DatabaseAccess;

import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.schedulers.Schedulers;


/**
 * Created by DFedonnikov on 24.08.2017.
 */

public class ListFragmentModelImpl implements IListFragmentModel  {

    private DatabaseAccess mDatabaseAccess;

    public ListFragmentModelImpl() {
        this.mDatabaseAccess = DatabaseAccess.getInstance(App.self());
    }

    @Override
    public void openDB() {
        mDatabaseAccess.open();
    }

    @Override
    public void closeDB() {
        mDatabaseAccess.close();
    }

    private <T> Observable<T> getObservableFromCallable(Callable<T> callable) {
        return Observable
                .fromCallable(callable)
                .subscribeOn(Schedulers.computation());
    }


    @Override
    public Observable<List<ClickableMemo>> getData() {
        return getObservableFromCallable(mDatabaseAccess.getAllMemos());
    }

    @Override
    public Observable<List<Integer>> deleteSelectedMemosFromDB(SparseArray<ClickableMemo> selectedMemos, List<ClickableMemo> memos) {
        return getObservableFromCallable(mDatabaseAccess.deleteSelected(selectedMemos, memos));
    }

    @Override
    public Observable<Boolean> deleteMemoFromDB(int memoId, int memoPosition, List<ClickableMemo> memos) {
        return getObservableFromCallable(mDatabaseAccess.delete(memoId, memoPosition, memos));
    }

    @Override
    public Observable<Void> swapMemos(int fromId, int fromPosition, int toId, int toPosition) {
        return getObservableFromCallable(mDatabaseAccess.swapMemos(fromId, fromPosition, toId, toPosition));
    }

    @Override
    public Observable<Void> setMemoAlarmFalse(int id) {
       return getObservableFromCallable(mDatabaseAccess.setMemoAlarmFalse(id));
    }

    @Override
    public Observable<Void> updateExpandedColumn(boolean itemsExpanded) {
        return getObservableFromCallable(mDatabaseAccess.updateExpandedColumns(itemsExpanded));
    }
}
