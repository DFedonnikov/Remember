package com.gnest.remember.data;

/**
 * Created by DFedonnikov on 08.07.2017.
 */

public class ClickableMemo extends Memo {
    private boolean mSelected = false;
    private boolean mExpanded = false;

    public ClickableMemo(int id, String memoText, int position, String color, boolean isSelected, boolean isExpanded) {
        super(id, memoText, position, color);
        this.mSelected = isSelected;
        this.mExpanded = isExpanded;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean mSelected) {
        this.mSelected = mSelected;
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public void setExpanded(boolean expanded) {
        this.mExpanded = expanded;
    }
}
