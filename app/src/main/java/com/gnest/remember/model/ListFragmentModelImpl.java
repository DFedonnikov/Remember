package com.gnest.remember.model;

import com.gnest.remember.App;
import com.gnest.remember.model.data.ClickableMemo;
import com.gnest.remember.model.db.DatabaseAccess;

import java.util.List;

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
    public Observable<List<ClickableMemo>> getData() {
        mDatabaseAccess.open();
        return Observable
                .just(mDatabaseAccess.getAllMemos())
                .subscribeOn(Schedulers.computation())
                .doOnUnsubscribe(() -> mDatabaseAccess.close());
    }

    @Override
    public void deleteMemoFromDB(int memoId, int memoPosition, List<ClickableMemo> memos) {
        mDatabaseAccess.open();
        mDatabaseAccess.delete(memoId, memoPosition, memos);
        mDatabaseAccess.close();
    }

    @Override
    public void swapMemos(int fromId, int fromPosition, int toId, int toPosition) {
        mDatabaseAccess.open();
        mDatabaseAccess.swapMemos(fromId, fromPosition, toId, toPosition);
        mDatabaseAccess.close();
    }

    @Override
    public void setMemoAlarmFalse(int id) {
        mDatabaseAccess.open();
        mDatabaseAccess.setMemoAlarmFalse(id);
        mDatabaseAccess.close();
    }

    @Override
    public void updateExpandedColumn(boolean itemsExpanded) {
        mDatabaseAccess.startUpdateExpandedColumnTask(itemsExpanded);
    }
}
