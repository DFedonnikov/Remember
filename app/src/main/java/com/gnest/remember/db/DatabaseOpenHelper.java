package com.gnest.remember.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE = "memos.db";
    static final String TABLE = "memos";
    private static final int VERSION = 2;

    DatabaseOpenHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE memos(_id INTEGER PRIMARY KEY AUTOINCREMENT, memo TEXT, position INTEGER, textViewBackgroundId INTEGER, textViewBackgroundSelectedId INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE memos ADD COLUMN textViewBackgroundId");
            db.execSQL("ALTER TABLE memos ADD COLUMN textViewBackgroundSelectedId");

        }
    }
}
