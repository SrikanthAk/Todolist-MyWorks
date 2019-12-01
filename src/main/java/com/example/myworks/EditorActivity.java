package com.example.myworks;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.myworks.data.WorkContract;
import com.example.myworks.data.WorkDBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.SimpleTimeZone;

public class EditorActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_WORK_LOADER = 0;
    private Uri mCurrentWorkUri;
    /** EditText field to enter the work title */
    private EditText mWorkTitleEditText;

    /** EditText field to enter the work details */
    private EditText mWorkDescEditText;

    /** field to enter the work date */
    private EditText mDatetTextView;

    /** field to enter the work time */
    private EditText mTimeTextView;

    private boolean mWorkHasChanged = false;

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mWorkHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
         mCurrentWorkUri = intent.getData();

        if(mCurrentWorkUri==null){
            setTitle(R.string.editor_activity_title_new_work);
            invalidateOptionsMenu();
        }else {
            setTitle(R.string.editor_activity_title_edit_work);
            getSupportLoaderManager().initLoader(EXISTING_WORK_LOADER, null, this);
        }

        mWorkTitleEditText = (EditText) findViewById(R.id.edit_work_title);
        mWorkDescEditText  = (EditText) findViewById(R.id.edit_work_desc);
        mDatetTextView = (EditText) findViewById(R.id.date);
        mTimeTextView = (EditText) findViewById(R.id.time);

        mWorkTitleEditText.setOnTouchListener(mOnTouchListener);
        mWorkDescEditText.setOnTouchListener(mOnTouchListener);
        mDatetTextView.setOnTouchListener(mOnTouchListener);
        mTimeTextView.setOnTouchListener(mOnTouchListener);

        mDatetTextView.setOnClickListener(this);
        mTimeTextView.setOnClickListener(this);


    }

    final Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate();
        }

    };

    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);
           updateTime(hourOfDay, minute);
        }
    };
    @Override
    public void onClick(View v) {
        if (v == mDatetTextView) {
            // TODO Auto-generated method stub
            new DatePickerDialog(EditorActivity.this, dateSetListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
        if(v==mTimeTextView){
            new TimePickerDialog(EditorActivity.this, timeSetListener,
                    myCalendar.get(Calendar.HOUR_OF_DAY),myCalendar.get(Calendar.MINUTE),
                    false).show();
        }
    }
    //displays date in edittext field
    public void updateDate(){
        String myFormat = "dd-MMM-yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        mDatetTextView.setText(sdf.format(myCalendar.getTime()));

    }
    //displays time in edittext field
    public void updateTime(int hours, int minutes){
        String myFormat = "hh:mm a"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        mTimeTextView.setText(sdf.format(myCalendar.getTime()));
    }
    /**
     * Get user input from the editor and save into worklog database
     */
    private void saveWork(){
        String workTitleString = mWorkTitleEditText.getText().toString().trim();
        String workDescString = mWorkDescEditText.getText().toString().trim();
        String dateString = mDatetTextView.getText().toString().trim();
        String timeString = mTimeTextView.getText().toString().trim();

        if((mCurrentWorkUri == null) && (workTitleString.isEmpty() || workDescString.isEmpty() || dateString.isEmpty()|| timeString.isEmpty())){
            Toast.makeText(this, "must fill all fields to save work!", Toast.LENGTH_LONG).show();
            return;
        }
        if((mCurrentWorkUri != null) && (workTitleString.isEmpty() || workDescString.isEmpty() || dateString.isEmpty()|| timeString.isEmpty())){
            Toast.makeText(this, "must fill all fields to update work!", Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(WorkContract.WorkEntry.COLUMN_WORK_TITLE,workTitleString);
        contentValues.put(WorkContract.WorkEntry.COLUMN_WORK_DESCRIPTION, workDescString);
        contentValues.put(WorkContract.WorkEntry.COLUMN_WORK_DATE, dateString);
        contentValues.put(WorkContract.WorkEntry.COLUMN_WORK_TIME, timeString);

        if(mCurrentWorkUri==null) {

            Uri uriInsert = getContentResolver().insert(WorkContract.WorkEntry.CONTENT_URI, contentValues);
            if (uriInsert == null) {
                Toast.makeText(this, "Error with adding work ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Work saved ", Toast.LENGTH_SHORT).show();
            }
        }else{
            int rowsAffected = getContentResolver().update(mCurrentWorkUri, contentValues, null, null);
            if(rowsAffected == 0){
                Toast.makeText(this, "Error with updating this work", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
         super.onPrepareOptionsMenu(menu);
// If this is a new work, hide the "Delete" menu item.
        if (mCurrentWorkUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_save:
                saveWork();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mWorkHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardOnClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardOnClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        //forming query using projection, selection, selectionArgs
        String [] projection = {WorkContract.WorkEntry._ID,
                WorkContract.WorkEntry.COLUMN_WORK_TITLE,
                WorkContract.WorkEntry.COLUMN_WORK_DESCRIPTION,
                WorkContract.WorkEntry.COLUMN_WORK_DATE,
                WorkContract.WorkEntry.COLUMN_WORK_TIME};
        // Perform this raw SQL query "SELECT * FROM works"
        // to get a Cursor that contains all rows from the works table.
        //we are using content provider query method using ContentResolver
        return new CursorLoader(this, mCurrentWorkUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if(cursor.moveToFirst()){
            int workTitleColumnIndex = cursor.getColumnIndex(WorkContract.WorkEntry.COLUMN_WORK_TITLE);
            int workDescColumnIndex = cursor.getColumnIndex(WorkContract.WorkEntry.COLUMN_WORK_DESCRIPTION);
            int workDateColumnIndex =  cursor.getColumnIndex(WorkContract.WorkEntry.COLUMN_WORK_DATE);
            int workTimeColumnIndex = cursor.getColumnIndex(WorkContract.WorkEntry.COLUMN_WORK_TIME);


            String workTitle = cursor.getString(workTitleColumnIndex);
            String workDesc = cursor.getString(workDescColumnIndex);
            String date = cursor.getString(workDateColumnIndex);
            String time =  cursor.getString(workTimeColumnIndex);

            mWorkTitleEditText.setText(workTitle);
            mWorkDescEditText.setText(workDesc);
            mDatetTextView.setText(date);
            mTimeTextView.setText(time);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the work.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if(!mWorkHasChanged){
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the work.
                deleteWork();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the work.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteWork() {
        if (mCurrentWorkUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentWorkUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, R.string.editor_delete_work_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.editor_delete_work_successful, Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }
}

