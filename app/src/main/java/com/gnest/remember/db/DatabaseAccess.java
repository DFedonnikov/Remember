package com.gnest.remember.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gnest.remember.data.Memo;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private SQLiteDatabase database;
    private DatabaseOpenHelper openHelper;
    private static volatile DatabaseAccess instance;

    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static synchronized DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public void save(Memo memo) {
        ContentValues values = new ContentValues();
        values.put("memo", memo.getMemoText());
        database.insert(DatabaseOpenHelper.TABLE, null, values);
    }

    public void update(Memo memo) {
        ContentValues values = new ContentValues();
        values.put("memo", memo.getMemoText());
        int pos = memo.getId();
        int rows = database.update(DatabaseOpenHelper.TABLE, values, "_id = ?", new String[]{String.valueOf(pos)});
    }

    public void delete(Memo memo) {
        int pos = memo.getId();
        database.delete(DatabaseOpenHelper.TABLE, "_id = ?", new String[]{String.valueOf(pos)});
    }

    public List<Memo> getAllMemos() {
        List<Memo> memos = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * From memo ORDER BY _id", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = Integer.valueOf(cursor.getString(0));
            String text = cursor.getString(1);
            memos.add(new Memo(id, text));
            cursor.moveToNext();
        }
        cursor.close();
        return memos;
    }
}
