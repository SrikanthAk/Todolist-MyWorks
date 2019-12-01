package com.example.myworks.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class WorkDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "worklog.db";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + WorkContract.WorkEntry.TABLE_NAME;


    public WorkDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + WorkContract.WorkEntry.TABLE_NAME + " (" +
                        WorkContract.WorkEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        WorkContract.WorkEntry.COLUMN_WORK_TITLE + " TEXT," +
                        WorkContract.WorkEntry.COLUMN_WORK_DESCRIPTION + " TEXT, " +
                        WorkContract.WorkEntry.COLUMN_WORK_DATE + " TEXT, " +
                        WorkContract.WorkEntry.COLUMN_WORK_TIME + " TEXT)";
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
