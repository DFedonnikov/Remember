package com.gnest.remember.db.dbtasks;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

/**
 * Created by DFedonnikov on 02.08.2017.
 */

public class UpdateExpandColumnTask extends AsyncTask<Boolean, Void, Void> {

    private SQLiteDatabase mDatabase;
    private String mDbName;

    public UpdateExpandColumnTask(SQLiteDatabase database, String dbName) {
        this.mDatabase = database;
        this.mDbName = dbName;
    }

    @Override
    protected Void doInBackground(Boolean... booleans) {
        ContentValues values = new ContentValues();
        int flag = booleans[0] ? 1 : 0;
        values.put("expanded", flag);
        mDatabase.update(mDbName, values, null, null);
        return null;
    }
}
