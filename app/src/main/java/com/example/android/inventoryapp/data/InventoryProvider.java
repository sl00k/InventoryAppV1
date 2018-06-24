package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.TextView;

public class InventoryProvider extends ContentProvider {

    private static final int INVENTORY = 100;
    private static final int INVENTORY_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    // Database Helper Object
    private InventoryDbHelper mDbHelper;

    // Initialize the provider and DB Helper Object
    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // get readable Database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        // Tis cursor will hold the result of the query
        Cursor cursor = null;

        //Figure out if the uri matcher can find the URI to a specific match
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                // For this code query the whole table. The cursor holds all available data
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case INVENTORY_ID:
                // extract the id from the given uri
                // the id will be stored in selectionArgs
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // now start a query on the table and get the entries at the given id
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("cannot query uri" + uri);
        }
        //set notification uri in cursor so if the data
        // changes the cursors gets updated
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Returns the MIMI type of data for the content URI
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return InventoryContract.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return InventoryContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri " + uri + " with match " + match);
        }
    }

    //Insert new data into the provider with the given content values
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertInventory(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for" + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // get writable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        // rowsDeleted to track number of rows deleted
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                //Delete all rows that match the selection and selectionArgs#
                rowsDeleted = database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID:
                // delete the row at the given id
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for" + uri);
        }
        // if one or more rows deleted then notify all listeners that data has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        //return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updateInventory(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                // get the id from the uri
                // selection will be "_id=?"
                // selectionArgs will be the id
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateInventory(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("update is not supported for" + uri);
        }
    }

    private Uri insertInventory(Uri uri, ContentValues values) {

        // get writable database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // The first argument for db.insert() is the pets table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.
        long id = db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);
        // if the id is -1 then the insertion failed. return null
        if (id == -1) {
            return null;
        }
        //notify all listeners that the data has changed for the inventory content uri
        getContext().getContentResolver().notifyChange(uri, null);
        // once we know the id of the new entry
        //return the new uri with the id appended to the end of it
        return ContentUris.withAppendedId(uri, id);

    }

    private int updateInventory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // if there are no values to update, then exit here
        if (values.size() == 0) {
            return 0;
        }
        // if values are present, get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // preform the update and get back the updated number of rows
        int rowsUpdated = database.update(InventoryContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
