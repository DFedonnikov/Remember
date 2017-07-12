package com.gnest.remember.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gnest.remember.data.Memo;
import com.gnest.remember.data.SelectableMemo;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private SQLiteDatabase database;
    private DatabaseOpenHelper openHelper;
    private static volatile DatabaseAccess instance;
    private static volatile int currentLastPosition = 0;

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
        values.put("position", currentLastPosition);
        long rowId = database.insert(DatabaseOpenHelper.TABLE, null, values);
        if (rowId != -1) {
            currentLastPosition++;
        }
    }

    public void update(Memo memo) {
        ContentValues values = new ContentValues();
        values.put("memo", memo.getMemoText());
        int id = memo.getId();
        database.update(DatabaseOpenHelper.TABLE, values, "_id = ?", new String[]{String.valueOf(id)});
    }

    public void delete(Memo memo) {
        int id = memo.getId();
        int rows = database.delete(DatabaseOpenHelper.TABLE, "_id = ?", new String[]{String.valueOf(id)});
        if (rows != 0) {
            currentLastPosition--;
        }
    }

    public void swipeMemos(SelectableMemo from, SelectableMemo to) {
        ContentValues values = new ContentValues();
        values.put("position", to.getPosition());
        int fromId = from.getId();
        database.update(DatabaseOpenHelper.TABLE, values, "_id = ?", new String[]{String.valueOf(fromId)});
        values.put("position", from.getPosition());
        int toId = to.getId();
        database.update(DatabaseOpenHelper.TABLE, values, "_id = ?", new String[]{String.valueOf(toId)});
    }


    public List<Memo> getAllMemos() {
        List<Memo> memos = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * From memo ORDER BY position", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = Integer.valueOf(cursor.getString(0));
            String text = cursor.getString(1);
            int position = Integer.valueOf(cursor.getString(2));
            memos.add(new Memo(id, text, position));
            cursor.moveToNext();
        }
        cursor.close();
        return memos;
    }
}
