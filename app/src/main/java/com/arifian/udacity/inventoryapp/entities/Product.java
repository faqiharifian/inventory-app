package com.arifian.udacity.inventoryapp.entities;

import android.database.Cursor;

import com.arifian.udacity.inventoryapp.data.InventoryContract.ProductEntry;

/**
 * Created by faqih on 25/04/17.
 */

public class Product {
    private String name, imageName;
    private int id, price, qty;
    byte[] imageBytes;

    public Product(int id, String name, String imageName, int price, int qty, byte[] imageBytes) {
        this.id = id;
        this.name = name;
        this.imageName = imageName;
        this.price = price;
        this.qty = qty;
        this.imageBytes = imageBytes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageName() {
        return imageName;
    }

    public int getPrice() {
        return price;
    }

    public int getQty() {
        return qty;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public static Product fromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));
        String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_NAME));
        String imageName = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_IMAGE_NAME));
        int price = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRICE));
        int qty = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_QTY));
        byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(ProductEntry.COLUMN_IMAGE));
        return new Product(id, name, imageName, price, qty, imageBytes);
    }
}
