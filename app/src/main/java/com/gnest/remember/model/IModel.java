package com.gnest.remember.model;

import com.gnest.remember.model.data.ClickableMemo;

import java.util.List;

/**
 * Created by DFedonnikov on 23.08.2017.
 */

public interface IModel {
    void getData();

    void deleteMemoFromDB(int memoId, int memoPosition, List<ClickableMemo> memos);
}
