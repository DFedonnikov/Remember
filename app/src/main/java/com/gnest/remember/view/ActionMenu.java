package com.gnest.remember.view;


import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gnest.remember.R;
import com.gnest.remember.data.SelectableMemo;
import com.gnest.remember.layout.ItemFragment;

/**
 * Created by DFedonnikov on 08.07.2017.
 */

public class ActionMenu implements ActionMode.Callback {
    private ItemFragment.OnItemListFragmentInteractionListener listener;
    private SelectableMemo memo;
    private View selectedView;

    public ActionMenu(ItemFragment.OnItemListFragmentInteractionListener listener, View selectedView, SelectableMemo memo) {
        this.listener = listener;
        this.selectedView = selectedView;
        this.memo = memo;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.context_menu_bar, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                listener.onEditButtonPressed(memo, mode);
                return true;
            case R.id.delete:
                memo.setSelected(false);
                listener.onDeleteButtonPressed(memo);
                return true;
            case R.id.share:
                listener.onShareButtonPressed(memo);

        }
        return false;
    }



    @Override
    public void onDestroyActionMode(ActionMode mode) {
        /*When action menu back button clicked to deselect memo, at the moment of executing of onDestroyActionMode()selectableMemo memo field will be still set to true.
        Deselection will be executed through calling onClick method of selectedView that was passed to ActionMenu constructor during creating context menu after selecting SelectableMemo;
        When memo itself clicked to be deselected, at the moment of executing of onDestroyActionMode() SelectableMemo memo field will be already set to false.
        This checking avoiding executing onClick method more than once.
        * */

        if (memo.isSelected()) {
            selectedView.callOnClick();
        }
    }
}
