package com.gnest.remember.model.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.gnest.remember.model.data.ClickableMemo;
import com.gnest.remember.model.db.DatabaseAccess;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DFedonnikov on 19.07.2017.
 */

public class DBLoader extends AsyncTaskLoader<List<ClickableMemo>> {

    private DatabaseAccess db;

    public DBLoader(Context context, DatabaseAccess db) {
        super(context);
        this.db = db;
    }

    @Override
    public List<ClickableMemo> loadInBackground() {
        if (db != null) {
            return db.getAllMemos();
        }
        return new ArrayList<>();
    }
}
