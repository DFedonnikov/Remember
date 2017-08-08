package com.gnest.remember.asynctasks;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.gnest.remember.db.DatabaseAccess;

/**
 * Created by DFedonnikov on 02.08.2017.
 */

public class UpdateExpandColumnTask extends AsyncTask<Boolean, Void, Void> {

    private DatabaseAccess mDatabaseAccess;

    public UpdateExpandColumnTask(DatabaseAccess databaseAccess) {
        this.mDatabaseAccess = databaseAccess;
    }

    @Override
    protected Void doInBackground(Boolean... booleans) {
        mDatabaseAccess.updateExpandedColumns(booleans[0]);
        return null;
    }
}
