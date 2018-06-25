package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the pet data loader
     */
    private static final int EXISTING_INVENTORY_LOADER = 0;

    private EditText mNameEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private EditText mSupplierEditText;
    private EditText mPhoneEditText;
    private Button mIncreaseButton;
    private Button mDecreaseButton;
    private Button mMakeCall;

    /**
     * Content uri for the existing item(in the table) (null if its a new item)
     */
    private Uri mCurrentInventoryUri;


    /**
     * Boolean flag that keeps track of whether the table has been edited (true) or not (false)
     */
    private boolean mInventoryHasChanged = false;
    // OnTouchListener to check if data has changed
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mInventoryHasChanged = true;
            return false;
        }
    };

    private View.OnClickListener mIncreaseListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int quantity = Integer.parseInt(mQuantityEditText.getText().toString());
            quantity = quantity + 1;
            ContentValues values = new ContentValues();
            values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);
            mQuantityEditText.setText(String.valueOf(quantity));
        }
    };
    private View.OnClickListener mDecreaseListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int quantity = Integer.parseInt(mQuantityEditText.getText().toString());
            int newQuantity = quantity - 1;
            if (newQuantity < 0) {
                Toast.makeText(getApplicationContext(), "Quantity can not be less than 0", Toast.LENGTH_LONG).show();
                newQuantity = quantity;
            }
            ContentValues values = new ContentValues();
            values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
            mQuantityEditText.setText(String.valueOf(newQuantity));
        }
    };

    private View.OnClickListener mMakeCallListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String phone = mPhoneEditText.getText().toString();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);
        }
    };

    private void saveItem() {
        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String phoneString = mPhoneEditText.getText().toString().trim();

        // check if strings are empty
        if (mCurrentInventoryUri == null && (TextUtils.isEmpty(nameString) ||
                TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(supplierString) || TextUtils.isEmpty(phoneString))) {
            Toast.makeText(this, "item not saved - blank entry", Toast.LENGTH_LONG).show();
            return;
        }
        // create contentValues object
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierString);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, phoneString);
        //check if in edit or add mode
        if (mCurrentInventoryUri == null) {
            // add mode, add  new row to the table
            Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);
            // check if the row was added
            if (newUri == null) {
                Toast.makeText(this, "Error with saving new Item", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "DB updated", Toast.LENGTH_SHORT).show();
            }
        } else
        // otherwise we have an existing item. edit mode
        {
            int rowsAffected = getContentResolver().update(mCurrentInventoryUri, values, null, null);
            // check if update was successfully
            if (rowsAffected == 0) {
                Toast.makeText(this, "Error with saving Item", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "DB updated", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void deleteItem() {
        // only delete if this is an existing item
        if (mCurrentInventoryUri != null) {
            //cal the contentResolver to delete the item at the given content uri
            int rowsDeleted = getContentResolver().delete(mCurrentInventoryUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "Error with deleting", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor_activity, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // on new item hide "delete" option
        if (mCurrentInventoryUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // check with option is selected
        switch (item.getItemId()) {
            case R.id.action_save:
                saveItem();

                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mInventoryHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                // we have unsaved changes
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // user clicked discard button, go to parent activity
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // if no changes happened, go back
        if (!mInventoryHasChanged) {
            super.onBackPressed();
            return;
        }
        //otherwise show dialog
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // user clicked discard button close the activity
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // shows dialog when deleting
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        // check if the intent is to edit or to add
        // new inventory
        Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();
        if (mCurrentInventoryUri == null) {
            setTitle(getString(R.string.add_mode_title));
            // in add mode we don't need the menu/delete option
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_mode_title));
            // int loader to get item info
            getSupportLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }
        // find all views that we will need to read user input from

        mNameEditText = findViewById(R.id.edit_name);
        mQuantityEditText = findViewById(R.id.edit_quantity);
        mPriceEditText = findViewById(R.id.edit_price);
        mSupplierEditText = findViewById(R.id.edit_supplier);
        mPhoneEditText = findViewById(R.id.edit_phone);
        //set onTouchListeners
        mNameEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);

        // Set standard quantity of 0
        mQuantityEditText.setText("0");
        // buttons to change quantity or make a call

        mIncreaseButton = findViewById(R.id.increase_quantity);
        mDecreaseButton = findViewById(R.id.reduce_quantity);
        mMakeCall = findViewById(R.id.call_supplier);

        mIncreaseButton.setOnClickListener(mIncreaseListener);
        mDecreaseButton.setOnClickListener(mDecreaseListener);
        mMakeCall.setOnClickListener(mMakeCallListener);
        mIncreaseButton.setOnTouchListener(mTouchListener);
        mDecreaseButton.setOnTouchListener(mTouchListener);
    }

    @NonNull
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // create projection
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };
        return new CursorLoader(this,
                mCurrentInventoryUri,   // Query the content URI
                projection,             // Columns to include in the resulting Cursor
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        // bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // find columns of item attributes
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
            //extract the value from the cursor
            String name = cursor.getString(nameColumnIndex);
            String quantity = cursor.getString(quantityColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);

            // update the views with the given attributes
            mNameEditText.setText(name);
            mQuantityEditText.setText(quantity);
            mPriceEditText.setText(price);
            mSupplierEditText.setText(supplier);
            mPhoneEditText.setText(phone);
        }
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {
        // if the loader is invalidated clear out all the data from the input fields
        mNameEditText.setText("");
        mQuantityEditText.setText("");
    }
}
