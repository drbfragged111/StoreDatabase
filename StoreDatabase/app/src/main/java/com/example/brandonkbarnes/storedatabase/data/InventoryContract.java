package com.example.brandonkbarnes.storedatabase.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Brandon on 12/13/2017.
 */

public final class InventoryContract {

    private InventoryContract() {}

    //The "Content authority" is a name for the entire content provider.
    public static final String CONTENT_AUTHORITY = "com.example.brandonkbarnes.storedatabase";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Possible path (appended to base content URI for possible URI's)
    public static final String PATH_INVENTORY = "inventory";

    /**
     * Inner class that defines constant values for the inventory database table.
     * Each entry in the table represents a single item.
     */
    public static final class InventoryEntry implements BaseColumns {

        //The content URI to access the item data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /** Name of database table for inventory */
        public final static String TABLE_NAME = "inventory";

        /**
         * Unique ID number for the item (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the item.
         *
         * Type: TEXT
         */
        public final static String COLUMN_ITEM_NAME ="name";

        /**
         * Price of the item.
         *
         * Type: TEXT
         */
        public final static String COLUMN_ITEM_PRICE = "price";

        /**
         * Quantity of the item.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_ITEM_QUANTITY = "quantity";

        /**
         * Picture of the item.
         *
         * Type: BLOB
         */
        public final static String COLUMN_ITEM_IMAGE = "image";

        /**
         * Item Supplier's name.
         *
         * Type: Text
         */
        public final static String COLUMN_ITEM_SUPPLIER_NAME = "supplier";

        /**
         * Item Supplier's email.
         *
         * Type: Text
         */
        public final static String COLUMN_ITEM_SUPPLIER_EMAIL = "email";

        /**
         * Item Supplier's phone number.
         *
         * Type: Text
         */
        public final static String COLUMN_ITEM_SUPPLIER_PHONE = "phone";
    }
}
