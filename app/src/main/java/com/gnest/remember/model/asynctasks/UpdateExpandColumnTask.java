package com.gnest.remember.model.asynctasks;

import android.os.AsyncTask;

import com.gnest.remember.model.db.DatabaseAccess;

/**
 * Created by DFedonnikov on 02.08.2017.
 */

public class UpdateExpandColumnTask extends AsyncTask<Boolean, Void, Void> {

    private DatabaseAccess mDatabaseAccess;

    public UpdateExpandColumnTask(DatabaseAccess databaseAccess) {
        this.mDatabaseAccess = databaseAccess;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDatabaseAccess.open();
    }

    @Override
    protected Void doInBackground(Boolean... booleans) {
        mDatabaseAccess.updateExpandedColumns(booleans[0]);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mDatabaseAccess.close();
    }
}
