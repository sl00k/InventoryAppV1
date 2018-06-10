package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {

    private InventoryContract() {
    }

    ;

    /* Inner class that defines the table contents of the inventory table */
    public static final class InventoryEntry implements BaseColumns {


        // Table name
        public static final String TABLE_NAME = "inventory";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "Product";
        public static final String COLUMN_PRODUCT_PRICE = "Price";
        public static final String COLUMN_PRODUCT_QUANTITY = "Quantity";
        public static final String COLUMN_PRODUCT_SUPPLIER_NAME = "Supplier";
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER = "Contact";
    }


}
