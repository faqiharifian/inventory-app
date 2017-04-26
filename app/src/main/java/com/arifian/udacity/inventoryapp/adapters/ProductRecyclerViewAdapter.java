package com.arifian.udacity.inventoryapp.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arifian.udacity.inventoryapp.DetailActivity;
import com.arifian.udacity.inventoryapp.R;
import com.arifian.udacity.inventoryapp.data.InventoryContract;
import com.arifian.udacity.inventoryapp.entities.Product;
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
        holder.priceTextView.setText(String.valueOf(product.getPrice()));
        holder.qtyTextView.setText(String.valueOf(product.getQty()));

        if(product.getImageBytes() != null)
            Glide.with(context).load(product.getImageBytes()).into(holder.productImageView);
        else
            holder.productImageView.setVisibility(View.GONE);

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
        if(this.cursor != cursor) {
            this.cursor = cursor;
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView productImageView;
        TextView nameTextView, priceTextView, qtyTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            productImageView = (ImageView) itemView.findViewById(R.id.image_product);
            nameTextView = (TextView) itemView.findViewById(R.id.text_product_name);
            priceTextView = (TextView)itemView.findViewById(R.id.text_product_price);
            qtyTextView = (TextView)itemView.findViewById(R.id.text_product_qty);
        }
    }
}
