package com.gnest.remember.view;

import android.support.v7.view.ActionMode;

import com.gnest.remember.model.db.data.Memo;
import com.gnest.remember.view.adapters.MySelectableAdapter;
import com.gnest.remember.view.fragments.ListItemFragment;
import com.gnest.remember.view.layoutmanagers.MyGridLayoutManager;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

import io.realm.RealmResults;

/**
 * Created by DFedonnikov on 08.09.2017.
 */

public interface IListFragmentView extends MvpView {
    void setData(RealmResults<Memo> data);

    void removeAlarm(int memoId);

    void shareMemoText(String memoText);

    MyGridLayoutManager getLayoutManager();

    ListItemFragment.OnListItemFragmentInteractionListener getInteractionListener();

    MySelectableAdapter getAdapter();

    ActionMode getActionMode();
}
