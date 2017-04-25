package com.arifian.udacity.inventoryapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {
    EditText saleEditText, receiveEditText;
    ImageButton saleIncreaseButton, saleDecreaseButton, receiveIncreaseButton, receiveDecreaseButton;
    TextView qtyTextView;
    AlertDialog.Builder builder;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        qtyTextView = (TextView) findViewById(R.id.text_product_qty);
        saleEditText = (EditText) findViewById(R.id.edit_sale_number);
        saleIncreaseButton = (ImageButton) findViewById(R.id.button_sale_increase);
        saleDecreaseButton = (ImageButton) findViewById(R.id.button_sale_decrease);
        receiveEditText = (EditText) findViewById(R.id.edit_receive_number);
        receiveIncreaseButton = (ImageButton) findViewById(R.id.button_receive_increase);
        receiveDecreaseButton = (ImageButton) findViewById(R.id.button_receive_decrease);

        builder = new AlertDialog.Builder(this);


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
                        saleEditText.setText("1");
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
                        receiveEditText.setText("1");
                        receiveEditText.selectAll();
                    }
                }
            }
        });

        findViewById(R.id.button_product_sale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!saleEditText.getText().toString().equals("")) {
                    qtyTextView.setText(String.valueOf(getInteger(qtyTextView) - getInteger(saleEditText)));
                    if (qtyTextView.getText().toString().equals("0")) {
                        findViewById(R.id.button_product_sale).setEnabled(false);
                        saleEditText.setText("0");
                    }else
                        saleEditText.setText("1");
                }
            }
        });
        findViewById(R.id.button_product_receive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!saleEditText.getText().toString().equals("")) {
                    qtyTextView.setText(String.valueOf(getInteger(qtyTextView) + getInteger(receiveEditText)));
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
                intent.putExtra(Intent.EXTRA_SUBJECT, "Order");
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.order_more_message, "Product name"));
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(DetailActivity.this, "Please install a mail client first.",
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

    public void increase(View view){
        switch (view.getId()){
            case R.id.button_sale_increase:
                if(!saleEditText.getText().toString().equals("")) {
                    if (getInteger(saleEditText) < getInteger(qtyTextView)) {
                        saleEditText.setText(String.valueOf(getInteger(saleEditText) + 1));
                    }
                }else{
                    if(qtyTextView.getText().toString().equals("0")){
                        saleEditText.setText("0");
                    }else{
                        saleEditText.setText("1");
                    }
                }
                break;
            case R.id.button_receive_increase:
                if(!receiveEditText.getText().toString().equals("")) {
                    receiveEditText.setText(String.valueOf(getInteger(receiveEditText) + 1));
                }else{
                    receiveEditText.setText("1");
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
                    if(qtyTextView.getText().toString().equals("0")){
                        saleEditText.setText("0");
                    }else{
                        saleEditText.setText("1");
                    }
                }
                break;
            case R.id.button_receive_decrease:
                if(!receiveEditText.getText().toString().equals("")) {
                    if(getInteger(receiveEditText) > 1){
                        receiveEditText.setText(String.valueOf(getInteger(receiveEditText) - 1));
                    }
                }else{
                    receiveEditText.setText("1");
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
