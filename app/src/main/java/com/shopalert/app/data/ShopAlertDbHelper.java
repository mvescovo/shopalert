package com.shopalert.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shopalert.app.data.ShopAlertContract.ProductEntry;
import com.shopalert.app.data.ShopAlertContract.ShopWithPriceEntry;
import com.shopalert.app.data.ShopAlertContract.UserProductEntry;

/**
 * Created by Michael Vescovo on 6/02/16.
 *
 * Manages a local database for ShopAlert data.
 *
 */
public class ShopAlertDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "shopalert.db";

    public ShopAlertDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " (" +
                ProductEntry._ID + " INTEGER PRIMARY KEY," +
                ProductEntry.COLUMN_PRODUCT_ID + " TEXT, " +
                ProductEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ProductEntry.COLUMN_DESCRIPTION + " TEXT, " +
                ProductEntry.COLUMN_IMAGE_URL + " TEXT," +

                "UNIQUE (" + ProductEntry.COLUMN_NAME + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_SHOP_WITH_PRICE_TABLE = "CREATE TABLE " + ShopWithPriceEntry.TABLE_NAME + " (" +
                ShopWithPriceEntry._ID + " INTEGER PRIMARY KEY," +
                ShopWithPriceEntry.COLUMN_SHOP_ID + " TEXT NOT NULL, " +
                ShopWithPriceEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ShopWithPriceEntry.COLUMN_LATITUDE + " TEXT NOT NULL, " +
                ShopWithPriceEntry.COLUMN_LONGITUDE + " TEXT NOT NULL," +
                ShopWithPriceEntry.COLUMN_IMAGE_URL + " TEXT," +
                ShopWithPriceEntry.COLUMN_PRICE_ID + " TEXT NOT NULL," +
                ShopWithPriceEntry.COLUMN_PRICE + " TEXT NOT NULL, " +
                ShopWithPriceEntry.COLUMN_PRODUCT_ID + " TEXT NOT NULL, " +

                "FOREIGN KEY (" + ShopWithPriceEntry.COLUMN_PRODUCT_ID + ") REFERENCES " +
                ProductEntry.TABLE_NAME + " (" + ProductEntry.COLUMN_PRODUCT_ID + "), " +

                "UNIQUE (" + ShopWithPriceEntry.COLUMN_PRODUCT_ID + ", " +
                ShopWithPriceEntry.COLUMN_SHOP_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_USER_PRODUCT_TABLE = "CREATE TABLE " + UserProductEntry.TABLE_NAME + " (" +
                UserProductEntry._ID + " INTEGER PRIMARY KEY," +
                UserProductEntry.COLUMN_USER_ID + " TEXT NOT NULL, " +
                UserProductEntry.COLUMN_PRODUCT_ID + " TEXT NOT NULL, " +

                "UNIQUE (" + UserProductEntry.COLUMN_USER_ID + ", " +
                UserProductEntry.COLUMN_PRODUCT_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SHOP_WITH_PRICE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_USER_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ShopWithPriceEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserProductEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
