package com.gnest.remember.helper;

import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by DFedonnikov on 16.07.2017.
 */

public interface ItemTouchHelperViewHolder {
    /**
     * Called when the {@link ItemTouchHelper} first registers an item as being moved or swiped.
     * Implementations should update the item view to indicate it's active state.
     */
    void setSelectedState();


    /**
     * Called when the {@link ItemTouchHelper} has completed the move or swipe, and the active item
     * state should be cleared.
     */
    void setDeselectedState();
}
