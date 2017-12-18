package com.example.brandonkbarnes.storedatabase.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.brandonkbarnes.storedatabase.data.InventoryContract.InventoryEntry;

/**
 * Created by Brandon on 12/13/2017.
 * Database helper for Store Database app.
 */

public class InventoryDbHelper  extends SQLiteOpenHelper{

    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

    // Name of the database file
    private static final String DATABASE_NAME = "store.db";

    //Database version.
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link InventoryDbHelper}.
     *
     * @param context of the app
     */
    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //This is called when the database is created for the first time.
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the inventory table
        String SQL_CREATE_INVENTORY_TABLE =  "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_ITEM_PRICE + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryEntry.COLUMN_ITEM_IMAGE + " BLOB, "
                + InventoryEntry.COLUMN_ITEM_SUPPLIER_NAME + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_ITEM_SUPPLIER_EMAIL + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_ITEM_SUPPLIER_PHONE + " TEXT NOT NULL);";
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    //This is called when the database needs to be upgraded.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
