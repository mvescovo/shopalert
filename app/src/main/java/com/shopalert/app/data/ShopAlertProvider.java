package com.shopalert.app.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.shopalert.app.application.MainActivity;

/**
 * Created by Michael Vescovo on 6/02/16.
 *
 */
public class ShopAlertProvider extends ContentProvider {
    private static final String TAG = "ShopAlertProvider";

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ShopAlertDbHelper mOpenHelper;

    static final int PRODUCT = 100;
    static final int PRODUCT_WITH_USER = 101;
    static final int USER_PRODUCT = 200;
    static final int SHOP_WITH_PRICE = 300;

    private static final SQLiteQueryBuilder sProductByUserQueryBuilder;

    static {
        sProductByUserQueryBuilder = new SQLiteQueryBuilder();

        // This looks like "product INNER JOIN userproduct ON product.productid = userproduct.productid
        sProductByUserQueryBuilder.setTables(
                ShopAlertContract.ProductEntry.TABLE_NAME + " INNER JOIN " +
                        ShopAlertContract.UserProductEntry.TABLE_NAME +
                        " ON " + ShopAlertContract.ProductEntry.TABLE_NAME +
                        "." + ShopAlertContract.ProductEntry.COLUMN_PRODUCT_ID +
                        " = " + ShopAlertContract.UserProductEntry.TABLE_NAME +
                        "." + ShopAlertContract.UserProductEntry.COLUMN_PRODUCT_ID
        );
    }

    private Cursor getProductByUser(String[] projection) {
        Log.i(TAG, "getProductByUser: going to return getproductbyuser query");
        String selection = ShopAlertContract.UserProductEntry.TABLE_NAME +
                "." + ShopAlertContract.UserProductEntry.COLUMN_USER_ID + " = ? ";
        String[] selectionArgs = new String[]{MainActivity.getmUserEmail()};

//                return sProductByUserQueryBuilder.query(mOpenHelper.getReadableDatabase(),
//                projection,
//                selection,
//                selectionArgs,
//                null,
//                null,
//                null
//        );

        return query(ShopAlertContract.ProductEntry.CONTENT_URI, null, null, null, null);
    }

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ShopAlertContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, ShopAlertContract.PATH_PRODUCT, PRODUCT);
        matcher.addURI(authority, ShopAlertContract.PATH_PRODUCT + "/*", PRODUCT_WITH_USER);
        matcher.addURI(authority, ShopAlertContract.PATH_USER_PRODUCT, USER_PRODUCT);
        matcher.addURI(authority, ShopAlertContract.PATH_SHOP_WITH_PRICE, SHOP_WITH_PRICE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ShopAlertDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCT:
                return ShopAlertContract.ProductEntry.CONTENT_DIR_TYPE;
            case PRODUCT_WITH_USER:
                return ShopAlertContract.ProductEntry.CONTENT_DIR_TYPE;
            case USER_PRODUCT:
                return ShopAlertContract.UserProductEntry.CONTENT_DIR_TYPE;
            case SHOP_WITH_PRICE:
                return ShopAlertContract.ShopWithPriceEntry.CONTENT_DIR_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case PRODUCT:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ShopAlertContract.ProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case PRODUCT_WITH_USER:
            {
                Log.i(TAG, "getType: product with user matched");
                retCursor = getProductByUser(projection);
                break;
            }

            case USER_PRODUCT:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ShopAlertContract.UserProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case SHOP_WITH_PRICE:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ShopAlertContract.ShopWithPriceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        // Just doing product for now.
        switch (match) {
            case PRODUCT: {
                long _id = db.insert(ShopAlertContract.ProductEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ShopAlertContract.ProductEntry.buildProductUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case USER_PRODUCT: {
                long _id = db.insert(ShopAlertContract.UserProductEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ShopAlertContract.UserProductEntry.buildUserProductUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case SHOP_WITH_PRICE: {
                long _id = db.insert(ShopAlertContract.ShopWithPriceEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ShopAlertContract.ShopWithPriceEntry.buildShopWithPriceUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case PRODUCT:
                rowsDeleted = db.delete(
                        ShopAlertContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case USER_PRODUCT:
                rowsDeleted = db.delete(
                        ShopAlertContract.UserProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SHOP_WITH_PRICE:
                rowsDeleted = db.delete(
                        ShopAlertContract.ShopWithPriceEntry.TABLE_NAME, selection, selectionArgs);
//            case PRICE:
//                rowsDeleted = db.delete(
//                        ShopAlertContract.PriceEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        // Just doing product for now
        switch (match) {
            case PRODUCT:
                Log.i(TAG, "update: going to update product");
                rowsUpdated = db.update(ShopAlertContract.ProductEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case USER_PRODUCT:
                rowsUpdated = db.update(ShopAlertContract.UserProductEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case SHOP_WITH_PRICE:
                rowsUpdated = db.update(ShopAlertContract.ShopWithPriceEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ShopAlertContract.ProductEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
