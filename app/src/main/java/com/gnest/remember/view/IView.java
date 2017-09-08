package com.gnest.remember.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.gnest.remember.model.data.ClickableMemo;
import com.gnest.remember.view.fragments.ListItemFragment;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

/**
 * Created by DFedonnikov on 23.08.2017.
 */

public interface IView extends MvpView {
    void setData(List<ClickableMemo> data);

    void removeAlarm(int memoId);

    void shareMemoText(String memoText);

    MyGridLayoutManager getLayoutManager();

    ListItemFragment.OnListItemFragmentInteractionListener getInteractionListener();
}
