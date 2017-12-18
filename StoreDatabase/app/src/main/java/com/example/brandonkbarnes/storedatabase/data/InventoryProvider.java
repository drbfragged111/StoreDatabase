package com.example.brandonkbarnes.storedatabase.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.brandonkbarnes.storedatabase.data.InventoryContract.InventoryEntry;

/**
 * Created by Brandon on 12/16/2017.
 * {@link ContentProvider} for Store Database app.
 */

public class InventoryProvider extends ContentProvider{

    /** Tag for the log messages */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the inventory table */
    private static final int ITEM = 100;

    /** URI matcher code for the content URI for a single item in the inventory table */
    private static final int ITEM_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // This URI is used to provide access to MULTIPLE rows of the inventory table.
        uriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, ITEM);

        // This URI is used to provide access to ONE single row of the pets table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        uriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", ITEM_ID);
    }

    /** Database helper object */
    private InventoryDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = uriMatcher.match(uri);
        switch (match) {
            case ITEM:
                // For the ITEM code, query the inventory table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the inventory table.
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ITEM_ID:
                // For the PET_ID code, extract out the ID from the URI.
                //
                // For every "?" in the selection, there needs to be an element in the selection
                // arguments that will fill in the "?". Since there is 1 question mark in the
                // selection, there is 1 String in the selection arguments' String array.
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the inventory table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case ITEM:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert an item into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertItem(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(InventoryEntry.COLUMN_ITEM_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Item requires a name");
        }

        // Check that the price is not null
        String price = values.getAsString(InventoryEntry.COLUMN_ITEM_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Item requires a price");
        }

        // Check that the quantity is not null and greater than or equal to zero
        Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_ITEM_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity requires valid quantity");
        }

        // No need to check the image, any value is valid (including null).

        // Check that the Supplier's name is not null
        String supName = values.getAsString(InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME);
        if (supName == null) {
            throw new IllegalArgumentException("Item requires a supplier name");
        }

        // Check that the Supplier's email is not null
        String supEmail = values.getAsString(InventoryEntry.COLUMN_ITEM_SUPPLIER_EMAIL);
        if (supEmail == null) {
            throw new IllegalArgumentException("Item requires a supplier email");
        }

        // Check that the Supplier's phone is not null
        String supPhone = values.getAsString(InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE);
        if (supPhone == null) {
            throw new IllegalArgumentException("Item requires a supplier phone number");
        }

        // Get writeable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Insert the new item with the given values
        long id = database.insert(InventoryEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the item content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case ITEM:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                // For the ITEM_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update items in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0, 1 or more items).
     * Return the number of rows that were successfully updated.
     */
    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link InventoryEntry#COLUMN_ITEM_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(InventoryEntry.COLUMN_ITEM_NAME)) {
            String name = values.getAsString(InventoryEntry.COLUMN_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }

        // If the {@link InventoryEntry#COLUMN_ITEM_PRICE} key is present,
        // check that the price is not null.
        if (values.containsKey(InventoryEntry.COLUMN_ITEM_PRICE)) {
            String price = values.getAsString(InventoryEntry.COLUMN_ITEM_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Item requires a price");
            }
        }

        // If the {@link InventoryEntry#COLUMN_ITEM_QUANTITY} key is present,
        // check that the quantity value is valid.
        if (values.containsKey(InventoryEntry.COLUMN_ITEM_QUANTITY)) {
            // Check that the quantity is not null and greater than or equal to 0
            Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_ITEM_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Item requires valid quantity");
            }
        }

        // No need to check the image, any value is valid (including null).

        // If the {@link InventoryEntry#COLUMN_ITEM_SUPPLIER_NAME} key is present,
        // check that the supplier's name value is not null.
        if (values.containsKey(InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME)) {
            String supName = values.getAsString(InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME);
            if (supName == null) {
                throw new IllegalArgumentException("Item requires a supplier name");
            }
        }

        // If the {@link InventoryEntry#COLUMN_ITEM_SUPPLIER_EMAIL} key is present,
        // check that the supplier's email is not null.
        if (values.containsKey(InventoryEntry.COLUMN_ITEM_SUPPLIER_EMAIL)) {
            String supEmail = values.getAsString(InventoryEntry.COLUMN_ITEM_SUPPLIER_EMAIL);
            if (supEmail == null) {
                throw new IllegalArgumentException("Item requires a supplier email");
            }
        }

        // If the {@link InventoryEntry#COLUMN_ITEM_SUPPLIER_PHONE} key is present,
        // check that the supplier's phone number value is not null.
        if (values.containsKey(InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE)) {
            String supPhone = values.getAsString(InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE);
            if (supPhone == null) {
                throw new IllegalArgumentException("Item requires a supplier phone number");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = uriMatcher.match(uri);
        switch (match) {
            case ITEM:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                // Delete a single row given by the ID in the URI
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case ITEM:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
