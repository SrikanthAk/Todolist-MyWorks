package com.example.myworks.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WorkProvider extends ContentProvider {
    public static final String LOG_TAG = WorkProvider.class.getSimpleName();
    private static final int WORKS = 100;
    private static final int WORKS_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(WorkContract.CONTENT_AUTHORITY, WorkContract.PATH_WORKS, WORKS);
        sUriMatcher.addURI(WorkContract.CONTENT_AUTHORITY, WorkContract.PATH_WORKS+"/#",WORKS_ID);
    }
    private WorkDBHelper mDBHelper;
    @Override
    public boolean onCreate() {
        mDBHelper = new WorkDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDBHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match){
            case WORKS:
                cursor = database.query(WorkContract.WorkEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case WORKS_ID:
                selection = WorkContract.WorkEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(WorkContract.WorkEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("can not query unknown query "+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case WORKS:
                return WorkContract.WorkEntry.CONTENT_LIST_TYPE;
            case WORKS_ID:
                return WorkContract.WorkEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case WORKS:
                    return insertWork(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for" +uri);
        }
    }

    private Uri insertWork(Uri uri, ContentValues contentValues){
        String workTitle = contentValues.getAsString(WorkContract.WorkEntry.COLUMN_WORK_TITLE);
        if(workTitle.isEmpty()){
            throw  new IllegalArgumentException("It requires work Title");
        }

        String workDesc = contentValues.getAsString(WorkContract.WorkEntry.COLUMN_WORK_DESCRIPTION);
        if(workDesc.isEmpty()){
            throw  new IllegalArgumentException("It requires work description");
        }


        String date = contentValues.getAsString(WorkContract.WorkEntry.COLUMN_WORK_DATE);
        if(date.isEmpty()){
            throw  new IllegalArgumentException("It requires date");
        }

        String time = contentValues.getAsString(WorkContract.WorkEntry.COLUMN_WORK_TIME);
        if(time.isEmpty()){
            throw  new IllegalArgumentException("It requires time");
        }

        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        long id = database.insert(WorkContract.WorkEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted = 0;
        final int match =  sUriMatcher.match(uri);
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        switch (match){
            case WORKS:
                rowsDeleted = database.delete(WorkContract.WorkEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case WORKS_ID:
                selection = WorkContract.WorkEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(WorkContract.WorkEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw  new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if(rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match =  sUriMatcher.match(uri);
        switch (match){
            case WORKS:
                return updateWork(uri, values, selection, selectionArgs);
            case WORKS_ID:
                 selection = WorkContract.WorkEntry._ID+ "=?";
                 selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                 return updateWork(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("update not supported for "+uri);
        }
    }

    private int updateWork(Uri uri, ContentValues contentValues, String selection, String [] selectionArgs){
       if(contentValues.containsKey(WorkContract.WorkEntry.COLUMN_WORK_TITLE)){
           String workTitle = contentValues.getAsString(WorkContract.WorkEntry.COLUMN_WORK_TITLE);
           if(workTitle.isEmpty()){
               throw  new IllegalArgumentException("It requires work Title");
           }
       }

       if(contentValues.containsKey(WorkContract.WorkEntry.COLUMN_WORK_DESCRIPTION)){
           String workDesc = contentValues.getAsString(WorkContract.WorkEntry.COLUMN_WORK_DESCRIPTION);
           if(workDesc.isEmpty()){
               throw  new IllegalArgumentException("It requires work description");
           }
       }


       if(contentValues.containsKey(WorkContract.WorkEntry.COLUMN_WORK_DATE)){
           String date = contentValues.getAsString(WorkContract.WorkEntry.COLUMN_WORK_DATE);
           if(date.isEmpty()){
               throw  new IllegalArgumentException("It requires work date");
           }
       }

       if(contentValues.containsKey(WorkContract.WorkEntry.COLUMN_WORK_TIME)){
           String time = contentValues.getAsString(WorkContract.WorkEntry.COLUMN_WORK_TIME);
           if(time.isEmpty()){
               throw  new IllegalArgumentException("It requires work time");
           }
       }


       if(contentValues.size() == 0){
           return 0;
       }
       int rows = 0;
       SQLiteDatabase database = mDBHelper.getWritableDatabase();
       rows = database.update(WorkContract.WorkEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if(rows!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rows;
    }
}
