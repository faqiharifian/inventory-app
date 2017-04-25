package com.arifian.udacity.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.arifian.udacity.inventoryapp.adapters.ProductRecyclerViewAdapter;
import com.arifian.udacity.inventoryapp.data.InventoryContract.ProductEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int PRODUCT_LOADER = 1;
    RecyclerView productsRecyclerView;
    ProductRecyclerViewAdapter adapter;
    TextView errorEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        errorEmptyTextView= (TextView) findViewById(R.id.text_error_empty);
        productsRecyclerView = (RecyclerView) findViewById(R.id.recycler_product);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        adapter = new ProductRecyclerViewAdapter(this);
        productsRecyclerView.setAdapter(adapter);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();
                cv.put(ProductEntry.COLUMN_NAME, "Product 1");
                cv.put(ProductEntry.COLUMN_PRICE, 10000);
                cv.put(ProductEntry.COLUMN_QTY, 10);
                getContentResolver().insert(ProductEntry.CONTENT_URI, cv);
//                startActivity(new Intent(MainActivity.this, EditActivity.class));
            }
        });

        getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                ProductEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        if (adapter.getItemCount() > 0) errorEmptyTextView.setVisibility(View.GONE);
        else errorEmptyTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
        errorEmptyTextView.setVisibility(View.VISIBLE);
    }
}
