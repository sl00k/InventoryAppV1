package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.InventoryDbHelper;

public class MainActivity extends AppCompatActivity {
    private InventoryDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbHelper = new InventoryDbHelper(this);
        displayDatabaseInfo();
    Button testBtn = findViewById(R.id.test_button);
    testBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            insertInventory();
            displayDatabaseInfo();
        }
    });
    }
    private void displayDatabaseInfo(){


        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
// projection and cursor to select items of the db
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };
        Cursor cursor = db.query(InventoryContract.InventoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
        try {

            TextView displayView = (TextView) findViewById(R.id.text_view_inventory);
            // Create a header in the Text View
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText("The table contains " + cursor.getCount() + " items.\n\n");
            displayView.append(InventoryContract.InventoryEntry._ID + " - " +InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME +" - "+ InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE+
                    " - "+InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY+" - "+ InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME
                    +" - "+ InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentPrice = cursor.getString(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);

                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentName+"-"+ currentPrice+"-"+currentQuantity+"-"+currentSupplierName+"-"+currentSupplierPhoneNumber));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }
    private void insertInventory(){
        String nameString = "Test Item";
        String priceString = "99";
        int quantityInt = 123;
        String supplierNameString = "Test Company";
        String supplierPhoneNumberInt = "01234567890";
        Toast.makeText(this,"insert called",Toast.LENGTH_LONG).show();
        // Gets the database in write mode
        InventoryDbHelper mDbHelper = new InventoryDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Create a ContentValues object where column names are the keys,
        // and Test-Item attributes are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantityInt);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierNameString);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberInt);


        long newRowId = db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);

        if(newRowId == -1){
            Toast.makeText(this,"Error with saving Item",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this,"DB updated",Toast.LENGTH_LONG).show();

        }
    }
}
