package com.arifian.udacity.inventoryapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {
    AlertDialog.Builder builderDelete;
    AlertDialog.Builder builderDiscard;
    String nameBefore, priceBefore, qtyBefore, imageBefore;
    EditText nameEditText, priceEditText, qtyEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameEditText = (EditText) findViewById(R.id.edit_product_name);
        priceEditText = (EditText) findViewById(R.id.edit_product_price);
        qtyEditText = (EditText) findViewById(R.id.edit_product_qty);

        nameBefore = nameEditText.getText().toString();
        priceBefore = priceEditText.getText().toString();
        qtyBefore = qtyEditText.getText().toString();

        builderDelete = new AlertDialog.Builder(this);
        builderDelete.setTitle(getString(R.string.dialog_delete_title));
        builderDelete.setMessage(getString(R.string.dialog_delete_message));
        builderDelete.setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: delete
            }
        });
        builderDelete.setNegativeButton(getString(R.string.dialog_negative), null);

        builderDiscard = new AlertDialog.Builder(this);
        builderDiscard.setTitle(getString(R.string.dialog_discard_title));
        builderDiscard.setMessage(getString(R.string.dialog_discard_message));
        builderDiscard.setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builderDiscard.setNegativeButton(getString(R.string.dialog_negative), null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
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
                startActivity(new Intent(EditActivity.this, DetailActivity.class));
                break;
            case R.id.action_delete:
                builderDelete.show();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(!(nameEditText.getText().toString().equals(nameBefore)
                && priceEditText.getText().toString().equals(priceBefore)
                && qtyEditText.getText().toString().equals(qtyBefore))){
            builderDiscard.show();
        }else{
            finish();
        }
    }
}
