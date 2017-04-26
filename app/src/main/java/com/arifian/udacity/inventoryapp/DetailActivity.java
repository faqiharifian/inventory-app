package com.arifian.udacity.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.arifian.udacity.inventoryapp.data.InventoryContract;
import com.arifian.udacity.inventoryapp.entities.Product;
import com.arifian.udacity.inventoryapp.utils.DialogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int PRODUCT_LOADER = 1;
    EditText saleEditText, receiveEditText;
    ImageButton saleIncreaseButton, saleDecreaseButton, receiveIncreaseButton, receiveDecreaseButton;
    AppBarLayout toolbarLayout;
    TextView qtyTextView, nameTextView, priceTextView;
    AlertDialog dialog;
    Uri currentUri;
    Product product;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        currentUri = getIntent().getData();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        toolbarLayout.setExpanded(false, true);
        nameTextView = (TextView) findViewById(R.id.text_product_name);
        priceTextView = (TextView) findViewById(R.id.text_product_price);
        qtyTextView = (TextView) findViewById(R.id.text_product_qty);
        saleEditText = (EditText) findViewById(R.id.edit_sale_number);
        saleIncreaseButton = (ImageButton) findViewById(R.id.button_sale_increase);
        saleDecreaseButton = (ImageButton) findViewById(R.id.button_sale_decrease);
        receiveEditText = (EditText) findViewById(R.id.edit_receive_number);
        receiveIncreaseButton = (ImageButton) findViewById(R.id.button_receive_increase);
        receiveDecreaseButton = (ImageButton) findViewById(R.id.button_receive_decrease);

        dialog = DialogUtil.create(this,
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

        saleEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saleEditText.selectAll();
            }
        });
        saleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!saleEditText.getText().toString().equals("")) {
                    if (saleEditText.getText().toString().equals("-") ||
                            (saleEditText.getText().toString().equals("0") && !qtyTextView.getText().equals("0"))) {
                        saleEditText.setText(getString(R.string.default_qty_sale));
                        saleEditText.selectAll();
                    } else if (getInteger(saleEditText) > getInteger(qtyTextView)) {
                            saleEditText.setText(qtyTextView.getText());
                            saleEditText.selectAll();
                    }
                }
            }
        });

        receiveEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiveEditText.selectAll();
            }
        });
        receiveEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!receiveEditText.getText().toString().equals("")) {
                    if (receiveEditText.getText().toString().equals("-") ||
                            receiveEditText.getText().toString().equals("0")) {
                        receiveEditText.setText(getString(R.string.default_qty_sale));
                        receiveEditText.selectAll();
                    }
                }
            }
        });

        findViewById(R.id.button_product_sale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!saleEditText.getText().toString().equals("")) {
                    updateQty(getInteger(qtyTextView) - getInteger(saleEditText));
                    saleEditText.setText(getString(R.string.default_qty_sale));
                }
            }
        });
        findViewById(R.id.button_product_receive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!saleEditText.getText().toString().equals("")) {
                    updateQty(getInteger(qtyTextView) + getInteger(receiveEditText));
                    findViewById(R.id.button_product_sale).setEnabled(true);
                }
            }
        });

        findViewById(R.id.text_product_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_more_text));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.order_more_message, "Product name"));
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(DetailActivity.this, getString(R.string.error_mail_client),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_edit:
                Intent intent = new Intent(this, EditActivity.class);
                intent.setData(currentUri);
                startActivity(intent);
                break;
            case R.id.action_delete:
                dialog.show();
                break;
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                currentUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()) {
            product = Product.fromCursor(data);
            nameTextView.setText(product.getName());
            priceTextView.setText(String.valueOf(product.getFormattedPrice()));
            qtyTextView.setText(String.valueOf(product.getQty()));
            if(product.getImageBytes() == null)
                toolbarLayout.setExpanded(false, true);
            else{
                toolbarLayout.setExpanded(true, true);
                Glide.with(this)
                        .load(product.getImageBytes())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                if(Build.VERSION.SDK_INT >= 16)
                                    toolbarLayout.setBackground(new BitmapDrawable(resource));
                            }
                        });
            }
            if (product.getQty() == 0)
                findViewById(R.id.button_product_sale).setEnabled(false);
        }else{
            Toast.makeText(this, getString(R.string.deleted), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void updateQty(int qty){
        ContentValues cv = new ContentValues();
        cv.put(InventoryContract.ProductEntry.COLUMN_QTY, qty);
        getContentResolver().update(currentUri, cv, null, null);
    }

    public void increase(View view){
        switch (view.getId()){
            case R.id.button_sale_increase:
                if(!saleEditText.getText().toString().equals("")) {
                    if (getInteger(saleEditText) < getInteger(qtyTextView)) {
                        saleEditText.setText(String.valueOf(getInteger(saleEditText) + 1));
                    }
                }else{
                    saleEditText.setText(getString(R.string.default_qty_sale));
                }
                break;
            case R.id.button_receive_increase:
                if(!receiveEditText.getText().toString().equals("")) {
                    receiveEditText.setText(String.valueOf(getInteger(receiveEditText) + 1));
                }else{
                    receiveEditText.setText(getString(R.string.default_qty_sale));
                }
                break;
        }
    }

    public void decrease(View view){
        switch (view.getId()){
            case R.id.button_sale_decrease:
                if(!saleEditText.getText().toString().equals("")) {
                    if(!qtyTextView.getText().toString().equals("0") && getInteger(saleEditText) > 1){
                        saleEditText.setText(String.valueOf(getInteger(saleEditText) - 1));
                    }
                }else{
                    saleEditText.setText(getString(R.string.default_qty_sale));
                }
                break;
            case R.id.button_receive_decrease:
                if(!receiveEditText.getText().toString().equals("")) {
                    if(getInteger(receiveEditText) > 1){
                        receiveEditText.setText(String.valueOf(getInteger(receiveEditText) - 1));
                    }
                }else{
                    receiveEditText.setText(getString(R.string.default_qty_sale));
                }
                break;
        }
    }

    private int getInteger(View view){
        int result = 0;
        if(view instanceof EditText || view instanceof TextView){
            result = Integer.valueOf(((TextView) view).getText().toString());
        }
        return result;
    }
}
