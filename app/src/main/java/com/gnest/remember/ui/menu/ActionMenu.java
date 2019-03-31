package com.gnest.remember.ui.menu;

import android.view.Menu;
import android.view.MenuItem;

import com.gnest.remember.R;
import com.gnest.remember.ui.fragments.ArchiveItemFragment;

import androidx.appcompat.view.ActionMode;

public class ActionMenu implements ActionMode.Callback {

    private MenuInteractionHelper listener;
    private MenuItem shareButton;

    public ActionMenu(MenuInteractionHelper listener) {
        this.listener = listener;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if (listener instanceof ArchiveItemFragment) {
            mode.getMenuInflater().inflate(R.menu.context_menu_bar_archive_frag, menu);
        } else {
            mode.getMenuInflater().inflate(R.menu.context_menu_bar_list_frag, menu);
        }
        listener.switchMultiSelect(true);
        shareButton = menu.findItem(R.id.share);
        return true;
    }


    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.archive:
                listener.onArchiveButtonPressed();
                return true;
            case R.id.delete:
                listener.onDeleteButtonPressed();
                return true;
            case R.id.share:
                listener.onShareButtonPressed();
                return true;
            default:
                return false;
        }
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {
        listener.switchMultiSelect(false);
        listener.clearSelection();

    }

    public void setShareButtonVisibility(boolean isVisible) {
        if (shareButton.isVisible() ^ isVisible) {
            shareButton.setVisible(isVisible);
        }
    }

    public interface MenuInteractionHelper {

        void onDeleteButtonPressed();

        void onShareButtonPressed();

        void switchMultiSelect(boolean switchedOn);

        void clearSelection();

        void onArchiveButtonPressed();
    }

}
