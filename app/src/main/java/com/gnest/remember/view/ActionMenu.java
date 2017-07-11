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

    private OnMenuItemClickedListener listener;

    public ActionMenu(OnMenuItemClickedListener listener) {
        this.listener = listener;
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
                listener.onEditButtonPressed();
                return true;
            case R.id.delete:
                listener.onDeleteButtonPressed();
                return true;
            case R.id.share:
                listener.onShareButtonPressed();

        }
        return false;
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {
        listener.onDeselectMemo();
    }

    public interface OnMenuItemClickedListener {
        void onEditButtonPressed();
        void onDeleteButtonPressed();
        void onShareButtonPressed();
        void onDeselectMemo();
    }

}
