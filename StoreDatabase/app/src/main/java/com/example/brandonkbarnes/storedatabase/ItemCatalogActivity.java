package com.example.brandonkbarnes.storedatabase;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.brandonkbarnes.storedatabase.data.InventoryContract.InventoryEntry;

public class ItemCatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    //Identifier for the item data loader
    private static final int ITEM_LOADER = 0;

    //Adapter for the ListView
    InventoryCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_catalog);

        //Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemCatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the item data
        ListView itemListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of item data in the Cursor.
        cursorAdapter = new InventoryCursorAdapter(this, null);
        itemListView.setAdapter(cursorAdapter);

        // Setup the item click listener
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(ItemCatalogActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific item that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link InventoryEntry#CONTENT_URI}.
                Uri currentItemUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentItemUri);

                // Launch the {@link EditorActivity} to display the data for the current item.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(ITEM_LOADER, null, this);


    }

    /**
     * Helper method to insert hardcoded item data into the database. For debugging purposes only.
     */
    private void insertItem() {
        // Create a ContentValues object where column names are the keys,
        // and Dawn's item attributes are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_ITEM_NAME, "Soap");
        values.put(InventoryEntry.COLUMN_ITEM_PRICE, "$3.00");
        values.put(InventoryEntry.COLUMN_ITEM_QUANTITY, 5);
        values.put(InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME, "Dawn");
        values.put(InventoryEntry.COLUMN_ITEM_SUPPLIER_EMAIL, "Dawn@Dawn.com");
        values.put(InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE, "555-867-5309");

        // Insert a new row for Dawn into the provider using the ContentResolver.
        // Use the {@link InventoryEntry#CONTENT_URI} to indicate that we want to insert
        // into the inventory database table.
        // Receive the new content URI that will allow us to access Dawn's data in the future.
        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all items in the database.
     */
    private void deleteAllItems() {
        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
        Log.v("ItemCatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the needed columns from the table.
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_ITEM_NAME,
                InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryEntry.COLUMN_ITEM_QUANTITY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                InventoryEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link InventoryCursorAdapter} with this new cursor containing updated item data
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        cursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_item_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_item_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertItem();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
