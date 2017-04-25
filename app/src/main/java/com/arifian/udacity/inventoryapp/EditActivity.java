package com.arifian.udacity.inventoryapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.arifian.udacity.inventoryapp.utils.FileUtil;
import com.arifian.udacity.inventoryapp.utils.PermissionUtil;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.Arrays;

public class EditActivity extends AppCompatActivity {
    private final int FILE_SELECT_CODE = 1;
    private String[] mimeTypes = new String[]{
        "image/jpeg", "image/png", "image/jpg"
    };
    AlertDialog.Builder builderDelete;
    AlertDialog.Builder builderDiscard;
    String nameBefore, priceBefore, qtyBefore;
    byte[] imageBytesBefore, imageBytes;
    EditText nameEditText, priceEditText, qtyEditText;
    TextView imageTextView;

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

        nameEditText = (EditText) findViewById(R.id.edit_product_name);
        priceEditText = (EditText) findViewById(R.id.edit_product_price);
        qtyEditText = (EditText) findViewById(R.id.edit_product_qty);
        imageTextView = (TextView) findViewById(R.id.text_product_image);

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
                    // Potentially direct the user to the Market with a Dialog
//                    showAlert("Aplikasi Tidak Ditemukan", "Silahkan install file manager terlebih dahulu.", "OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(Intent.ACTION_VIEW);
//                            intent.setData(Uri.parse("market://search?q=file%20manager&c=apps"));
//                            startActivity(intent);
//                        }
//                    });
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
                && qtyEditText.getText().toString().equals(qtyBefore))
                && imageBytes.equals(imageBytesBefore)){
            builderDiscard.show();
        }else{
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri attachmentUri = data.getData();
                    String attachmentPath = FileUtil.getPath(this, attachmentUri);
                    File attachment = new File(attachmentPath);
                    if(!Arrays.asList(mimeTypes).contains(FileUtil.getType(this, attachmentUri))){
                        imageTextView.setText("");
                        imageBytes = imageBytesBefore;

//                        showAlert("Gagal", "Silakan pilih file .xls .xlsx .doc .docx .pdf.", "OK", null);
                    }else{
                        imageTextView.setText(attachment.getName());
                        imageBytes = FileUtil.toByteArray(attachment);
                        Glide.with(this).load(attachment).into((ImageView) findViewById(R.id.image_product));
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
