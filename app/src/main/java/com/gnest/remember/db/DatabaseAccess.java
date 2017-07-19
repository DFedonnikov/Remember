package com.gnest.remember.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gnest.remember.data.Memo;
import com.gnest.remember.data.SelectableMemo;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private DatabaseOpenHelper openHelper;
    private SQLiteDatabase database;
    private static volatile DatabaseAccess instance;
    private static volatile int currentLastPosition = 0;

    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
        open();
        Cursor cursor = database.rawQuery("SELECT IFNULL(max(position), -1) From memos", null);
        if (cursor.moveToFirst()) {
            currentLastPosition = cursor.getInt(0) + 1;
        }
        cursor.close();
        close();
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
        values.put("textViewBackgroundId", memo.gettextViewBackgroundId());
        values.put("textViewBackgroundSelectedId", memo.getTextViewBackgroundSelectedId());
        long rowId = database.insert(DatabaseOpenHelper.TABLE, null, values);
        if (rowId != -1) {
            currentLastPosition++;
        }
    }

    public void update(Memo memo) {
        ContentValues values = new ContentValues();
        values.put("memo", memo.getMemoText());
        values.put("textViewBackgroundId", memo.gettextViewBackgroundId());
        values.put("textViewBackgroundSelectedId", memo.getTextViewBackgroundSelectedId());
        int id = memo.getId();
        database.update(DatabaseOpenHelper.TABLE, values, "_id = ?", new String[]{String.valueOf(id)});
    }

    public void delete(Memo memo, List<SelectableMemo> mMemos, int position) {
        int id = memo.getId();
        int rows = database.delete(DatabaseOpenHelper.TABLE, "_id = ?", new String[]{String.valueOf(id)});
        if (rows != 0) {
            currentLastPosition--;
        }
        updatePositionAfterDelete(mMemos, position);
    }

    private void updatePositionAfterDelete(List<SelectableMemo> mMemos, int position) {
        ContentValues values = new ContentValues();
        for (int i = position + 1; i < mMemos.size(); i++) {
            SelectableMemo memoToUpdate = mMemos.get(i);
            values.put("position", i - 1);
            int id = memoToUpdate.getId();
            database.update(DatabaseOpenHelper.TABLE, values, "_id = ?", new String[]{String.valueOf(id)});
        }
    }

    public void swapMemos(SelectableMemo from, SelectableMemo to) {
        ContentValues values = new ContentValues();
        values.put("position", to.getPosition());
        int fromId = from.getId();
        database.update(DatabaseOpenHelper.TABLE, values, "_id = ?", new String[]{String.valueOf(fromId)});
        values.put("position", from.getPosition());
        int toId = to.getId();
        database.update(DatabaseOpenHelper.TABLE, values, "_id = ?", new String[]{String.valueOf(toId)});
    }


    public List<SelectableMemo> getAllMemos() {
        List<SelectableMemo> memos = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * From memos ORDER BY position", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = Integer.valueOf(cursor.getString(0));
            String text = cursor.getString(1);
            int position = cursor.getInt(2);
            int textViewBackgroundId = cursor.getInt(3);
            int textViewBackgroundSelectedId = cursor.getInt(4);
            memos.add(new SelectableMemo(id, text, position, textViewBackgroundId, textViewBackgroundSelectedId, false));
            cursor.moveToNext();
        }
        cursor.close();
        return memos;
    }
}
