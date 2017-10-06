package com.lancius.palle2patnam.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "palle2patnam";

    // table name
    private static final String TABLE_NAME = "categories";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_CAT_ID = "catid";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_TITLE = "title";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_QTY = "qty";
    private static final String KEY_PRICE = "price";
    private static final String KEY_TOTAL_PRICE = "total_price";

    String quantity, weight;
    Double total = 0.0, total_price = 0.0;
    List<String> sumPriceList;
    List<String> sumQtyList;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CAT_ID + " TEXT," + KEY_IMAGE + " TEXT," + KEY_TITLE + " TEXT," + KEY_WEIGHT + " TEXT," + KEY_QTY + " TEXT,"
                + KEY_PRICE + " TEXT," + KEY_TOTAL_PRICE + " TEXT" + ")";

        db.execSQL(CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    public void addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CAT_ID, category.getCatId()); // Contact Name
        values.put(KEY_IMAGE, category.getImage()); // Contact Name
        values.put(KEY_TITLE, category.getTitle()); // Contact Name
        values.put(KEY_WEIGHT, category.getWeight()); // Contact Phone
        values.put(KEY_QTY, category.getQty()); // Contact Name
        values.put(KEY_PRICE, category.getPrice()); // Contact Phone
        values.put(KEY_TOTAL_PRICE, category.getTotalPrice()); // Contact Phone

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }


    // Deleting single contact
    public void deleteCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_CAT_ID + " = ?" + " AND " + KEY_PRICE + " = ?",
                new String[]{String.valueOf(category.getCatId()), String.valueOf(category.getPrice())});
        db.close();
    }


    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TABLE_CONTACTS,null,null);
        //db.execSQL("delete  from"+ TABLE_CONTACTS);
        db.execSQL("delete from " + TABLE_NAME);
        db.close();
    }

    public void updateQuantity(String cat_id, String sel_price, String qty) {
        SQLiteDatabase db = this.getWritableDatabase();

//        String query = "UPDATE categories SET qty = " + qty + ",  price = " + total_amt + " WHERE catid =" + cat_id;
//        Log.d("query", query);
//        Cursor cursor = db.rawQuery(query, null);
//
//        getQty(cat_id);

        ContentValues values = new ContentValues();
        values.put(KEY_CAT_ID, cat_id); // Contact Name
        values.put(KEY_PRICE, sel_price); // Contact Name
        values.put(KEY_QTY, qty); // Contact Name

        // Inserting Row
        db.update(TABLE_NAME, values, KEY_CAT_ID + "=" + cat_id + " AND " + KEY_PRICE + "=" + sel_price, null);
        db.close(); // Closing database connection

        //getQty(cat_id);
    }

//    public void updateQuantity(String cat_id, String qty) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
////        String query = "UPDATE categories SET qty = " + qty + ",  price = " + total_amt + " WHERE catid =" + cat_id;
////        Log.d("query", query);
////        Cursor cursor = db.rawQuery(query, null);
////
////        getQty(cat_id);
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_CAT_ID, cat_id); // Contact Name
//        values.put(KEY_QTY, qty); // Contact Name
//
//        // Inserting Row
//        db.update(TABLE_NAME, values, KEY_CAT_ID + "=" + cat_id, null);
//        db.close(); // Closing database connection
//
//        //getQty(cat_id);
//    }

    public String getQty(String cat_id, String sel_price) {

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM categories WHERE catid =" + cat_id + " AND price = " + sel_price;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            quantity = cursor.getString(cursor.getColumnIndex("qty"));
        }

        if (quantity == null) {
            quantity = "0";
        }

        return quantity;
    }

    public String getSelPosition(String cat_id, String sel_price) {

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM categories WHERE catid =" + cat_id + " AND price = " + sel_price;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            quantity = cursor.getString(cursor.getColumnIndex("id"));
        }

        if (quantity == null) {
            quantity = "0";
        }

        return quantity;
    }

    public String getProductImage(String cat_id) {

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM categories WHERE catid =" + cat_id;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            quantity = cursor.getString(cursor.getColumnIndex("image"));
        }

        if (quantity == null) {
            quantity = "0";
        }

        return quantity;
    }

    public Double getTotalPrice() {

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        sumPriceList = new ArrayList<>();
        sumQtyList = new ArrayList<>();
        total_price = 0.0;

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                String tprice = cursor.getString(cursor
                        .getColumnIndex("price"));
                String qty = cursor.getString(cursor
                        .getColumnIndex("qty"));

                sumPriceList.add(tprice);
                sumQtyList.add(qty);

//                Log.d("QTYSIZE", "" + sumQtyList.size());
//                Log.d("PRICESIZE", "" + sumPriceList.size());

                cursor.moveToNext();
            }
        }

        for (int i = 0; i < sumPriceList.size(); i++) {

            String price = sumPriceList.get(i);
            String qty = sumQtyList.get(i);
            total_price = Double.parseDouble(price) * Integer.parseInt(qty) + total_price;
//            Log.d("SUMTOTAL", "" + total_price);
        }


        return total_price;
    }

//    public String checkProductExists(String cat_id) {
//
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        String query = "SELECT * FROM categories WHERE catid =" + cat_id;
//        Cursor cursor = db.rawQuery(query, null);
//
//        if (cursor.getCount() <= 0) {
//            cursor.close();
//            return "No";
//        }
//
//        cursor.close();
//        return "Yes";
//
//    }

    public String checkProductExistsWithWeight(String cat_id, String id_price) {

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM categories WHERE catid =" + cat_id + " AND price = " + id_price;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            return "No";
        }

        cursor.close();
        return "Yes";

    }

    // Getting All Contacts
    public List<Category> getAllCategories() {
        List<Category> contactList = new ArrayList<Category>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Category contact = new Category();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setCatID(cursor.getString(1));
                contact.setImage(cursor.getString(2));
                contact.setTitle(cursor.getString(3));
                contact.setWeight(cursor.getString(4));
                contact.setQty(cursor.getString(5));
                contact.setPrice(cursor.getString(6));
                contact.setTotalPrice(cursor.getString(7));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

}
