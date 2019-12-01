package com.example.myworks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myworks.data.WorkContract;
import com.example.myworks.data.WorkDBHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int WORK_LOADER = 1;
    WorksCursorAdapter mWorksCursorAdapter;
    private WorkDBHelper mDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        //Setting FAB button to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        // and pass the context, which is the current activity.
        mDbHelper = new WorkDBHelper(this);
        ListView workListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        workListView.setEmptyView(emptyView);
        mWorksCursorAdapter = new WorksCursorAdapter(this, null);
        workListView.setAdapter(mWorksCursorAdapter);

        workListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentWorkURI = ContentUris.withAppendedId(WorkContract.WorkEntry.CONTENT_URI, id);
                intent.setData(currentWorkURI);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(WORK_LOADER, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog,menu);
        return true;
    }

    private void insertWork(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(WorkContract.WorkEntry.COLUMN_WORK_TITLE, "Buy Milk");
        contentValues.put(WorkContract.WorkEntry.COLUMN_WORK_DESCRIPTION, "Go to shop");
        contentValues.put(WorkContract.WorkEntry.COLUMN_WORK_DATE, "31-Oct-2019");
        contentValues.put(WorkContract.WorkEntry.COLUMN_WORK_TIME, "11:20 PM");
        Uri newUri =  getContentResolver().insert(WorkContract.WorkEntry.CONTENT_URI, contentValues);
        Log.v("CatalogActivity", "New row ID: " );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                // Do nothing for now
                insertWork();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllWorks();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteAllWorks(){
        int rowsDeleted = getContentResolver().delete(WorkContract.WorkEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + "rows Deleted from the works table");
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
        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        //we are using content provider query method using ContentResolver
        return new CursorLoader(this, WorkContract.WorkEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mWorksCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mWorksCursorAdapter.swapCursor(null);
    }
}
