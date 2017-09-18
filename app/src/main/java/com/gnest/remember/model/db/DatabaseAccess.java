package com.gnest.remember.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.Pair;
import android.util.SparseArray;

import com.gnest.remember.model.data.Memo;
import com.gnest.remember.model.data.ClickableMemo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

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
        if (database == null || !database.isOpen())
            database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null && database.isOpen()) {
            this.database.close();
        }
    }

    public Callable<Pair<Integer, Integer>> save(Memo memo) {
        return () -> {
            ContentValues values = new ContentValues();
            values.put("memo", memo.getMemoText());
            values.put("position", currentLastPosition);
            values.put("color", memo.getColor());
            values.put("alarmSet", memo.isAlarmSet() ? 1 : 0);
            long rowId = database.insert(DatabaseOpenHelper.TABLE, null, values);
            if (rowId != -1) {
                currentLastPosition++;
            }
            return new Pair<>((int) rowId, -1);
        };
    }

    public Callable<Pair<Integer, Integer>> update(ClickableMemo memo) {
        return () -> {
            ContentValues values = new ContentValues();
            values.put("memo", memo.getMemoText());
            values.put("color", memo.getColor());
            values.put("alarmSet", memo.isAlarmSet() ? 1 : 0);
            values.put("expanded", memo.isExpanded() ? 1 : 0);
            int id = memo.getId();
            database.update(DatabaseOpenHelper.TABLE, values, "_id = ?", new String[]{String.valueOf(id)});
            return new Pair<>(id, memo.getPosition());
        };
    }

    public Callable<List<Integer>> deleteSelected(SparseArray<ClickableMemo> selectedMemos, List<ClickableMemo> memos) {
        return () -> {
            List<Integer> deletedIds = new ArrayList<>();
            for (int i = 0; i < selectedMemos.size(); i++) {
                ClickableMemo memo = selectedMemos.valueAt(i);
                int row = database.delete(DatabaseOpenHelper.TABLE, "_id = ?", new String[]{String.valueOf(memo.getId())});
                if (row != 0) {
                    deletedIds.add(memo.getId());
                    currentLastPosition--;
                    updatePositionAfterDelete(memos, memo.getPosition());
                }
            }
            return deletedIds;
        };
    }

    public Callable<Boolean> delete(int memoId, int memoPosition, List<ClickableMemo> memos) {
        return () -> {
            int rows = database.delete(DatabaseOpenHelper.TABLE, "_id = ?", new String[]{String.valueOf(memoId)});
            if (rows != 0) {
                currentLastPosition--;
                updatePositionAfterDelete(memos, memoPosition);
            }
            return rows > 0;
        };
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


    public Callable<Void> setMemoAlarmFalse(int id) {
        return () -> {
            ContentValues values = new ContentValues();
            values.put("alarmSet", 0);
            database.update(DatabaseOpenHelper.TABLE, values, "_id = ?", new String[]{String.valueOf(id)});
            return null;
        };
    }

    public Callable<Void> swapMemos(int fromId, int fromPosition, int toId, int toPosition) {
        return () -> {
            synchronized (this) {
                ContentValues values = new ContentValues();
                values.put("position", toPosition);
                database.update(DatabaseOpenHelper.TABLE, values, "_id = ?", new String[]{String.valueOf(fromId)});
                values.put("position", fromPosition);
                database.update(DatabaseOpenHelper.TABLE, values, "_id = ?", new String[]{String.valueOf(toId)});
                return null;
            }
        };
    }

    public Callable<List<ClickableMemo>> getAllMemos() {
        return () -> {
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
        };

    }
}
