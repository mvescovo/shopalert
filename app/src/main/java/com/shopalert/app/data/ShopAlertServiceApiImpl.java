/*
 * Copyright 2015, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modifications have been made.
 */

package com.shopalert.app.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.shopalert.app.application.MainActivity;
import com.shopalert.app.gcm.RegistrationIntentService;
import com.shopalert.app.util.VolleyRequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael Vescovo on 6/02/16.
 *
 */
public class ShopAlertServiceApiImpl implements ShopAlertServiceApi {

    private static final String TAG = "ShopAlertServiceApiImpl";
    private Context mContext;

    private static final String[] PRODUCT_USER_COLUMNS = {
            ShopAlertContract.ProductEntry.TABLE_NAME + "." + ShopAlertContract.ProductEntry.COLUMN_NAME,
            ShopAlertContract.ProductEntry.TABLE_NAME + "." + ShopAlertContract.ProductEntry.COLUMN_DESCRIPTION,
            ShopAlertContract.ProductEntry.TABLE_NAME + "." + ShopAlertContract.ProductEntry.COLUMN_IMAGE_URL,
            ShopAlertContract.ProductEntry.TABLE_NAME + "." + ShopAlertContract.ProductEntry.COLUMN_PRODUCT_ID,
            ShopAlertContract.UserProductEntry.TABLE_NAME + "." + ShopAlertContract.UserProductEntry.COLUMN_USER_ID,
    };

    static final int COL_NAME = 0;
    static final int COL_DESCRIPTION = 1;
    static final int COL_IMAGE_URL = 2;
    static final int COL_PRODUCT_ID = 3;
    static final int COL_USER_ID = 4;


    @Override
    public void getAllProducts(Context context, final ProductServiceCallback<List<Product>> callback) {

        // Get products from local DB
        mContext = context;
        List<Product> products = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                ShopAlertContract.ProductEntry.buildProductUser(MainActivity.getmUserEmail()),
                PRODUCT_USER_COLUMNS,
                null,
                null,
                null
        );

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(ShopAlertContract.ProductEntry.COLUMN_NAME);
            int descriptionIndex = cursor.getColumnIndex(ShopAlertContract.ProductEntry.COLUMN_DESCRIPTION);
            int imageurlIndex = cursor.getColumnIndex(ShopAlertContract.ProductEntry.COLUMN_IMAGE_URL);
            int productidIndex = cursor.getColumnIndex(ShopAlertContract.ProductEntry.COLUMN_PRODUCT_ID);

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIndex);
                String description = cursor.getString(descriptionIndex);
                String image_url = cursor.getString(imageurlIndex);
                String productid = cursor.getString(productidIndex);
                Product product = new Product(name, description, image_url, productid);
                products.add(product);
            }
            cursor.close();
        }
        callback.onLoaded(products);
    }

    @Override
    public void getAllShopsWithPrices(Context context, String productId, ShopWithPriceServiceCallback<List<ShopWithPrice>> callback) {
        Log.i(TAG, "getAllShopsWithPrices: productid from getallshopswithprices: " + productId);
        mContext = context;

        // Get shopswithprices from local DB
        if (productId != null) {
            String selection = ShopAlertContract.ShopWithPriceEntry.COLUMN_PRODUCT_ID + " = ?";
            String[] selectionArgs = {productId};

            List<ShopWithPrice> shopsWithPrices = new ArrayList<>();

            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(
                    ShopAlertContract.ShopWithPriceEntry.CONTENT_URI,
                    null,
                    selection,
                    selectionArgs,
                    null
            );
            if (cursor != null) {
                int shopIdIndex = cursor.getColumnIndex(ShopAlertContract.ShopWithPriceEntry.COLUMN_SHOP_ID);
                int nameIndex = cursor.getColumnIndex(ShopAlertContract.ShopWithPriceEntry.COLUMN_NAME);
                int latitudeIndex = cursor.getColumnIndex(ShopAlertContract.ShopWithPriceEntry.COLUMN_LATITUDE);
                int longitudeIndex = cursor.getColumnIndex(ShopAlertContract.ShopWithPriceEntry.COLUMN_LONGITUDE);
                int imageUrlIndex = cursor.getColumnIndex(ShopAlertContract.ShopWithPriceEntry.COLUMN_IMAGE_URL);
                int priceIdIndex = cursor.getColumnIndex(ShopAlertContract.ShopWithPriceEntry.COLUMN_PRICE_ID);
                int priceIndex = cursor.getColumnIndex(ShopAlertContract.ShopWithPriceEntry.COLUMN_PRICE);

                while (cursor.moveToNext()) {
                    String shopId = cursor.getString(shopIdIndex);
                    String name = cursor.getString(nameIndex);
                    String latitude = cursor.getString(latitudeIndex);
                    String longitude = cursor.getString(longitudeIndex);
                    String imageUrl = cursor.getString(imageUrlIndex);
                    String priceId = cursor.getString(priceIdIndex);
                    String price = cursor.getString(priceIndex);

                    ShopWithPrice shopWithPrice = new ShopWithPrice(name, latitude, longitude, imageUrl, price, productId);
                    shopsWithPrices.add(shopWithPrice);
                }
                cursor.close();
            }
            callback.onLoaded(shopsWithPrices);
        } else {
            callback.onLoaded(new ArrayList<ShopWithPrice>());
        }
    }

    @Override
    public void saveProduct(Context context, Product product) {
        Log.i(TAG, "saveProduct: doing save product");
        mContext = context;
        long rowid;

        // Save product to local DB
//        rowid = saveProductToSQLite(product);

        // Save product to RDS
        saveProductToRDS(product);
    }

//    public long saveProductToSQLite(Product product) {
//        // Save product to product table
//        Uri mNewUri;
//        ContentResolver contentResolver = mContext.getContentResolver();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(ShopAlertContract.ProductEntry.COLUMN_NAME, product.getName());
//        contentValues.put(ShopAlertContract.ProductEntry.COLUMN_DESCRIPTION, product.getDescription());
//        contentValues.put(ShopAlertContract.ProductEntry.COLUMN_IMAGE_URL, "https://s3.amazonaws.com/modernlook.com-cdn/products/0/0/0/400/446/s1/soapstone_pizza_stone_12_2215.jpg");
////        contentValues.put(ShopAlertContract.ProductEntry.COLUMN_IMAGE_URL, "http://htmlcolorcodes.com/assets/images/html-color-codes-color-tutorials-hero-00e10b1f.jpg");
////        contentValues.put(ShopAlertContract.ProductEntry.COLUMN_IMAGE_URL, "https://bytesizemoments.com/wp-content/uploads/2014/04/placeholder.png");
//        mNewUri = contentResolver.insert(ShopAlertContract.ProductEntry.CONTENT_URI, contentValues);
//
//        return ContentUris.parseId(mNewUri);
//    }

    public void saveProductToRDS(final Product product) {
        Log.i(TAG, "saveProductToRDS: doing save product to rds");
//        final String url = new Uri.Builder().scheme("https")
//                .authority("s3459317-rmit.appspot.com")
//                .appendPath("addproduct.php")
//                .appendQueryParameter("userid", "m.vescovo@gmail.com")
//                .appendQueryParameter("name", product.getName())
//                .appendQueryParameter("description", product.getDescription())
//                .build().toString();

        String url = "https://s3459317-rmit.appspot.com/addproduct.php?userid=m.vescovo@gmail.com&name=" + product.getName() + "&description=" + product.getDescription();
        Log.i(TAG, "onResponse: url to json is: " + url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: got reponse from save product to rds" + response.toString());

                        try {
                            product.setProductId(response.getString("productid"));
                            Log.i(TAG, "onResponse: after setting the product id to local product it is: " + product.getProductId());
                            // Save productid to local DB
                            saveProductToSQLite(product);

                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: couldn't get productid from appengine", e);;
                            e.printStackTrace();
                        }
                        // Run the script on remote server to fetch shopsWithPrices
                        runGetPricesScript();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onErrorResponse: " + error.toString());
                    }
                });
        VolleyRequestQueue.getInstance(mContext).addToRequestQueue(jsObjRequest);
    }

    public void saveProductToSQLite(Product product) {
        Log.i(TAG, "saveProductIdToSQLite: product id from save to sqlite: " + product.getProductId());
        ContentResolver contentResolver = mContext.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ShopAlertContract.ProductEntry.COLUMN_PRODUCT_ID, product.getProductId());
        contentValues.put(ShopAlertContract.ProductEntry.COLUMN_NAME, product.getName());
        contentValues.put(ShopAlertContract.ProductEntry.COLUMN_DESCRIPTION, product.getDescription());
        contentValues.put(ShopAlertContract.ProductEntry.COLUMN_IMAGE_URL, "https://s3.amazonaws.com/modernlook.com-cdn/products/0/0/0/400/446/s1/soapstone_pizza_stone_12_2215.jpg");
//        contentValues.put(ShopAlertContract.ProductEntry.COLUMN_IMAGE_URL, "http://htmlcolorcodes.com/assets/images/html-color-codes-color-tutorials-hero-00e10b1f.jpg");
//        contentValues.put(ShopAlertContract.ProductEntry.COLUMN_IMAGE_URL, "https://bytesizemoments.com/wp-content/uploads/2014/04/placeholder.png");
        contentResolver.insert(ShopAlertContract.ProductEntry.CONTENT_URI, contentValues);

        // Save product and user to userproduct in local DB
        saveUserProductToSQLite(product.getProductId(), MainActivity.getmUserEmail());
    }

    public void saveUserProductToSQLite(String productid, String userEmail) {
        Log.i(TAG, "saveUserProductToSQLite: productid: " + productid + " useremail: " + userEmail);
        // Save mUserEmail and productid to userproduct table
        ContentResolver contentResolver = mContext.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ShopAlertContract.UserProductEntry.COLUMN_USER_ID, userEmail);
        contentValues.put(ShopAlertContract.UserProductEntry.COLUMN_PRODUCT_ID,productid);
        contentResolver.insert(ShopAlertContract.UserProductEntry.CONTENT_URI, contentValues);
    }

    public void runGetPricesScript() {
//        String url = "http://104.140.60.104:8080/shopalert?token=" + RegistrationIntentService.sToken;
        String url = "http://104.140.60.104:8080/shopalert?token=" + RegistrationIntentService.sToken + "&password=cloudcomputing2016";
//        String url = "http://14.201.3.163:9090/shopalert?token=" + RegistrationIntentService.sToken + "&password=cloudcomputing2016";
        Log.i(TAG, "runGetPricesScript: url: " + url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "run get prices cript error from volley: " + error.toString());
                    }
                });
        VolleyRequestQueue.getInstance(mContext).addToRequestQueue(jsObjRequest);
    }
}
