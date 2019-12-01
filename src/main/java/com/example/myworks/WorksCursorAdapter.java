package com.example.myworks;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.myworks.data.WorkContract;

public class WorksCursorAdapter extends CursorAdapter {

    public WorksCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView workTitleTextView = (TextView) view.findViewById(R.id.list_workTitle);
        TextView workDescTextView = (TextView) view.findViewById(R.id.list_WorkDesc);
        TextView workDateTextView = (TextView) view.findViewById(R.id.list_date);
        TextView workTimeTextView = (TextView) view.findViewById(R.id.list_time);

        int workTitleColumnIndex = cursor.getColumnIndex(WorkContract.WorkEntry.COLUMN_WORK_TITLE);
        int workDescColumnIndex = cursor.getColumnIndex(WorkContract.WorkEntry.COLUMN_WORK_DESCRIPTION);
        int workDateColumnIndex =  cursor.getColumnIndex(WorkContract.WorkEntry.COLUMN_WORK_DATE);
        int workTimeColumnIndex = cursor.getColumnIndex(WorkContract.WorkEntry.COLUMN_WORK_TIME);


        String workTitle = cursor.getString(workTitleColumnIndex);
        String workDesc = cursor.getString(workDescColumnIndex);
        String date = cursor.getString(workDateColumnIndex);
        String time =  cursor.getString(workTimeColumnIndex);

        workTitleTextView.setText(workTitle);
        workDescTextView.setText(workDesc);
        workDateTextView.setText(date);
        workTimeTextView.setText(time);

    }
}
