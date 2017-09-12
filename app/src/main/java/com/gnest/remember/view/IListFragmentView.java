package com.gnest.remember.view;

import com.gnest.remember.model.data.ClickableMemo;
import com.gnest.remember.view.fragments.ListItemFragment;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

/**
 * Created by DFedonnikov on 08.09.2017.
 */

public interface IListFragmentView extends MvpView {
    void setData(List<ClickableMemo> data);

    void removeAlarm(int memoId);

    void shareMemoText(String memoText);

    MyGridLayoutManager getLayoutManager();

    ListItemFragment.OnListItemFragmentInteractionListener getInteractionListener();

    MySelectableAdapter getAdapter();
}
