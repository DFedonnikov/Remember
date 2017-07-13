package com.gnest.remember.data;

/**
 * Created by DFedonnikov on 08.07.2017.
 */

public class SelectableMemo extends Memo {
    private boolean isSelected = false;

    public SelectableMemo(int id, String memoText, int position, boolean isSelected) {
        super(id, memoText, position);
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
