package com.arifian.udacity.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arifian.udacity.inventoryapp.data.InventoryContract.ProductEntry;
import com.arifian.udacity.inventoryapp.entities.Product;
import com.arifian.udacity.inventoryapp.utils.DialogUtil;
import com.arifian.udacity.inventoryapp.utils.FileUtil;
import com.arifian.udacity.inventoryapp.utils.PermissionUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.util.Arrays;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PRODUCT_LOADER = 3;
    private final int FILE_SELECT_CODE = 1;
    private String[] mimeTypes = new String[]{
        "image/jpeg", "image/png", "image/jpg"
    };
    AlertDialog deleteDialog;
    AlertDialog discardDialog;
    AlertDialog dynamicErrorDialog;
    String nameBefore, priceBefore, qtyBefore, imageNameBefore;
    byte[] imageBytesBefore = null, imageBytes = null;
    EditText nameEditText, priceEditText, qtyEditText;
    TextView imageTextView;
    Uri currentUri;
    Product product;
    private ImageView productImage;

    @Override
    protected void onResume() {
        PermissionUtil.askPermissions(this);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUri = getIntent().getData();
        if (currentUri != null){
            getSupportActionBar().setTitle(getString(R.string.title_edit_product));
            getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }else
            getSupportActionBar().setTitle(getString(R.string.title_add_product));

        nameEditText = (EditText) findViewById(R.id.edit_product_name);
        priceEditText = (EditText) findViewById(R.id.edit_product_price);
        qtyEditText = (EditText) findViewById(R.id.edit_product_qty);
        imageTextView = (TextView) findViewById(R.id.text_product_image);
        productImage = (ImageView) findViewById(R.id.image_product);

        nameBefore = nameEditText.getText().toString();
        priceBefore = priceEditText.getText().toString();
        qtyBefore = qtyEditText.getText().toString();

        deleteDialog = DialogUtil.create(this,
                getString(R.string.dialog_delete_title),
                getString(R.string.dialog_delete_message),
                true,
                getString(R.string.dialog_positive),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getContentResolver().delete(currentUri, null, null);
                    }
                },
                getString(R.string.dialog_negative),
                null
        );

        discardDialog = DialogUtil.create(this,
                getString(R.string.dialog_discard_title),
                getString(R.string.dialog_discard_message),
                true,
                getString(R.string.dialog_positive),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                },
                getString(R.string.dialog_negative),
                null
        );

        dynamicErrorDialog = DialogUtil.create(this,
                getString(R.string.dialog_error_title),
                "",
                true,
                getString(R.string.dialog_positive),
                null,
                null,
                null
        );

        findViewById(R.id.button_product_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(
                            Intent.createChooser(intent, "Select"),
                            FILE_SELECT_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(EditActivity.this, "Please install a File Manager.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(currentUri == null)
            menu.findItem(R.id.action_delete).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_done:
                boolean success = false;
                try {
                    if (currentUri == null)
                        getContentResolver().insert(ProductEntry.CONTENT_URI, getCV());
                    else
                        getContentResolver().update(currentUri, getCV(), null, null);
                    success = true;
                } catch (IllegalArgumentException iae){
                    dynamicErrorDialog.setMessage(iae.getMessage());
                    dynamicErrorDialog.show();
                }
                if(success) finish();
                break;
            case R.id.action_delete:
                deleteDialog.show();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(!(nameEditText.getText().toString().equals(nameBefore)
                && priceEditText.getText().toString().equals(priceBefore)
                && qtyEditText.getText().toString().equals(qtyBefore)
                && imageBytes == imageBytesBefore)){
            discardDialog.show();
        }else{
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    String imagePath = FileUtil.getPath(this, imageUri);
                    File imageFile = new File(imagePath);
                    if(!Arrays.asList(mimeTypes).contains(FileUtil.getType(this, imageUri))){
                        Toast.makeText(EditActivity.this, "Please select .jpg, .jpeg, .png file", Toast.LENGTH_LONG).show();
                    }else{
                        imageTextView.setText(imageFile.getName());
                        Glide.with(this)
                            .load(imageFile)
                            .asBitmap()
                            .toBytes(Bitmap.CompressFormat.JPEG, 80)
                            .atMost()
                            .fitCenter()
                            .override(1024, 1024)
                                .into(new SimpleTarget<byte[]>() {
                                    @Override
                                    public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                                        imageBytes = resource;
                                    }
                                });
                        Glide.with(this).load(imageFile).into(productImage);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private ContentValues getCV(){
        ContentValues cv = new ContentValues();
        cv.put(ProductEntry.COLUMN_NAME, nameEditText.getText().toString());
        cv.put(ProductEntry.COLUMN_PRICE, priceEditText.getText().toString());
        cv.put(ProductEntry.COLUMN_QTY, qtyEditText.getText().toString());
        cv.put(ProductEntry.COLUMN_IMAGE_NAME, imageTextView.getText().toString());
        cv.put(ProductEntry.COLUMN_IMAGE, imageBytes);
        return cv;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, currentUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()) {
            product = Product.fromCursor(data);
            nameEditText.setText(product.getName());
            nameBefore = product.getName();
            priceEditText.setText(String.valueOf(product.getPrice()));
            priceBefore = String.valueOf(product.getPrice());
            qtyEditText.setText(String.valueOf(product.getQty()));
            qtyBefore = String.valueOf(product.getQty());
            if(product.getImageBytes() != null) {
                Glide.with(this).load(product.getImageBytes()).into(productImage);
                imageNameBefore = product.getImageName();
                imageTextView.setText(product.getImageName());
            }
        }else{
            Toast.makeText(this, getString(R.string.deleted), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
