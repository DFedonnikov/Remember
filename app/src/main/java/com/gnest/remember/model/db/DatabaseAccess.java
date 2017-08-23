package com.gnest.remember.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gnest.remember.model.data.Memo;
import com.gnest.remember.model.data.ClickableMemo;
import com.gnest.remember.model.asynctasks.UpdateExpandColumnTask;

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

    public long save(Memo memo) {
        ContentValues values = new ContentValues();
        values.put("memo", memo.getMemoText());
        values.put("position", currentLastPosition);
        values.put("color", memo.getColor());
        values.put("alarmSet", memo.isAlarmSet() ? 1 : 0);
        long rowId = database.insert(DatabaseOpenHelper.TABLE, null, values);
        if (rowId != -1) {
            currentLastPosition++;
            return rowId;
        }
        return -1;
    }

    public void update(ClickableMemo memo) {
        ContentValues values = new ContentValues();
        values.put("memo", memo.getMemoText());
        values.put("color", memo.getColor());
        values.put("alarmSet", memo.isAlarmSet() ? 1 : 0);
        values.put("expanded", memo.isExpanded() ? 1 : 0);
        int id = memo.getId();
        database.update(DatabaseOpenHelper.TABLE, values, "_id = ?", new String[]{String.valueOf(id)});
    }

    public void delete(ClickableMemo memo, List<ClickableMemo> mMemos, int position) {
        if (!database.isOpen()) {
            open();
        }
        int id = memo.getId();
        int rows = database.delete(DatabaseOpenHelper.TABLE, "_id = ?", new String[]{String.valueOf(id)});
        if (rows != 0) {
            currentLastPosition--;
        }
        updatePositionAfterDelete(mMemos, position);
    }

    private void updatePositionAfterDelete(List<ClickableMemo> mMemos, int position) {
        ContentValues values = new ContentValues();
        for (int i = position + 1; i < mMemos.size(); i++) {
            ClickableMemo memoToUpdate = mMemos.get(i);
            values.put("position", i - 1);
            int id = memoToUpdate.getId();
            database.update(DatabaseOpenHelper.TABLE, values, "_id = ?", new String[]{String.valueOf(id)});
        }
    }

    public void setMemoAlarmFalse(int id) {
        ContentValues values = new ContentValues();
        values.put("alarmSet", 0);
        database.update(DatabaseOpenHelper.TABLE, values, "_id = ?", new String[]{String.valueOf(id)});
    }


    public void startUpdateExpandedColumnTask(boolean itemsExpanded) {
        new UpdateExpandColumnTask(this).execute(itemsExpanded);
    }

    public void updateExpandedColumns(Boolean expanded) {
        if (!database.isOpen()) {
            open();
        }
        ContentValues values = new ContentValues();
        int flag = expanded ? 1 : 0;
        values.put("expanded", flag);
        database.update(DatabaseOpenHelper.TABLE, values, null, null);
    }

    public void swapMemos(ClickableMemo from, ClickableMemo to) {
        ContentValues values = new ContentValues();
        values.put("position", to.getPosition());
        int fromId = from.getId();
        database.update(DatabaseOpenHelper.TABLE, values, "_id = ?", new String[]{String.valueOf(fromId)});
        values.put("position", from.getPosition());
        int toId = to.getId();
        database.update(DatabaseOpenHelper.TABLE, values, "_id = ?", new String[]{String.valueOf(toId)});
    }

    public List<ClickableMemo> getAllMemos() {
        List<ClickableMemo> memos = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * From memos ORDER BY position", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = Integer.valueOf(cursor.getString(0));
            String text = cursor.getString(1);
            int position = cursor.getInt(2);
            String color = cursor.getString(3);
            boolean isAlarmSet = cursor.getInt(4) == 1;
            boolean expanded = cursor.getInt(5) == 1;
            memos.add(new ClickableMemo(id, text, position, color, isAlarmSet, false, expanded));
            cursor.moveToNext();
        }
        cursor.close();
        return memos;
    }
}
