package com.gnest.remember.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.gnest.remember.data.SelectableMemo;
import com.gnest.remember.db.DatabaseAccess;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DFedonnikov on 19.07.2017.
 */

public class DBLoader extends AsyncTaskLoader<List<SelectableMemo>> {

    private DatabaseAccess db;

    public DBLoader(Context context, DatabaseAccess db) {
        super(context);
        this.db = db;
    }

    @Override
    public List<SelectableMemo> loadInBackground() {
        if (db != null) {
            return db.getAllMemos();
        }
        return new ArrayList<>();
    }
}
