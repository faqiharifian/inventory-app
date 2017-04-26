package com.arifian.udacity.inventoryapp.adapters;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.arifian.udacity.inventoryapp.DetailActivity;
import com.arifian.udacity.inventoryapp.R;
import com.arifian.udacity.inventoryapp.data.InventoryContract;
import com.arifian.udacity.inventoryapp.entities.Product;
import com.arifian.udacity.inventoryapp.utils.DialogUtil;
import com.bumptech.glide.Glide;

/**
 * Created by faqih on 26/04/17.
 */

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ViewHolder>{
    Context context;
    Cursor cursor = null;

    public ProductRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_products, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        final Product product = Product.fromCursor(cursor);
        holder.nameTextView.setText(product.getName());
        holder.priceTextView.setText(String.valueOf(product.getFormattedPrice()));
        holder.qtyTextView.setText(String.valueOf(product.getQty()));

        if(product.getImageBytes() != null)
            Glide.with(context).load(product.getImageBytes()).into(holder.productImageView);
        else
            holder.productImageView.setVisibility(View.GONE);

        if (product.getQty() == 0) holder.saleButton.setEnabled(false);
        else holder.saleButton.setEnabled(true);

        holder.saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri uri = ContentUris.withAppendedId(InventoryContract.ProductEntry.CONTENT_URI, product.getId());
                    ContentValues cv = new ContentValues();
                    cv.put(InventoryContract.ProductEntry.COLUMN_QTY, product.getQty() - 1);
                    context.getContentResolver().update(uri, cv, null, null);
                }catch (IllegalArgumentException iae){
                    DialogUtil.create(context,
                            context.getString(R.string.dialog_error_title),
                            iae.getMessage(),
                            true,
                            context.getString(R.string.dialog_positive),
                            null,
                            null,
                            null
                    ).show();
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(InventoryContract.ProductEntry.CONTENT_URI, product.getId());
                intent.setData(currentProductUri);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (cursor == null)
            return 0;
        else
            return cursor.getCount();
    }

    public void swapCursor(Cursor cursor){
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView productImageView;
        TextView nameTextView, priceTextView, qtyTextView;
        Button saleButton;

        public ViewHolder(View itemView) {
            super(itemView);
            productImageView = (ImageView) itemView.findViewById(R.id.image_product);
            nameTextView = (TextView) itemView.findViewById(R.id.text_product_name);
            priceTextView = (TextView)itemView.findViewById(R.id.text_product_price);
            qtyTextView = (TextView)itemView.findViewById(R.id.text_product_qty);
            saleButton = (Button) itemView.findViewById(R.id.button_product_sale);
        }
    }
}
