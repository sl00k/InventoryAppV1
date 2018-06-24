package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.R;

public class InventoryCursorAdapter extends CursorAdapter {
    /**
     * @param context the context
     * @param c       the cursor from which to get the data
     */
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the item data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {


        // find fields to populate in inflated template
        TextView nameView = view.findViewById(R.id.name);
        TextView quantityView = view.findViewById(R.id.quantity);
        TextView priceView = view.findViewById(R.id.price);
        // extract properties from the cursor
        final int id = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry._ID));
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);

        // read attributes from the current ITEM
        String itemName = cursor.getString(nameColumnIndex);
        final String itemQuantity = cursor.getString(quantityColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);


        // setup sell button
        Button mSellButton;
        mSellButton = view.findViewById(R.id.btn_sell);
        mSellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(itemQuantity);
                int newQuantity = quantity - 1;

                if (newQuantity < 0) {
                    Toast.makeText(context, "Quantity can not be less than 0", Toast.LENGTH_LONG).show();
                    newQuantity = quantity;
                }
                //update DB
                ContentValues contentValues = new ContentValues();
                contentValues.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);

                context.getContentResolver().update(ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id), contentValues, null, null);

            }
        });

// update the textviews

        nameView.setText(itemName);
        quantityView.setText(itemQuantity);
        priceView.setText(itemPrice);

    }
}
