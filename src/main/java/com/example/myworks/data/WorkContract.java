package com.example.myworks.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class WorkContract {

    public WorkContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.myworks";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_WORKS = "works";

    public static class WorkEntry implements BaseColumns{

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +"/"+CONTENT_AUTHORITY+ "/"+ PATH_WORKS;
        public static final  String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_WORKS;

        /** The content URI to access the work data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_WORKS);


        public final static String TABLE_NAME = "works";

        public final static String String_ID = BaseColumns._ID;
        public final static String COLUMN_WORK_TITLE = "workTitle";
        public final static String COLUMN_WORK_DESCRIPTION = "workDescription";
        public final static String COLUMN_WORK_DATE = "date";
        public final static String COLUMN_WORK_TIME = "time";
    }
}
