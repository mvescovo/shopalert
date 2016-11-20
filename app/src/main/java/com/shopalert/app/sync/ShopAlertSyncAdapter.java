package com.shopalert.app.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.shopalert.app.application.MainActivity;
import com.shopalert.app.data.ShopAlertContract;
import com.shopalert.app.geofence.Constants;
import com.shopalert.app.geofence.GeofenceTransitionsIntentService;
import com.shopalert.app.shopalert.R;
import com.shopalert.app.util.VolleyRequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Michael Vescovo on 6/02/16.
 *
 */
public class ShopAlertSyncAdapter extends AbstractThreadedSyncAdapter implements ResultCallback<Status>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "ShopAlertSyncAdapter";

    private final AccountManager mAccountManager;
    protected ArrayList<Geofence> mGeofenceList;
    protected GoogleApiClient mGoogleApiClient;
    private PendingIntent mGeofencePendingIntent;
    private ResultCallback<Status> mStatusResultCallback;

    public ShopAlertSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mAccountManager = AccountManager.get(context);
        mGeofenceList = new ArrayList<>();
        mGeofencePendingIntent = null;
        mStatusResultCallback = this;
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "Starting sync");
        Log.i(TAG, "onPerformSync: email from MAIN: " + MainActivity.getmUserEmail());

        /*
        *  Load products from RDS into the local db. There could be extras from other devices.
        *
        * */
        loadProductsFromRDS();

        /*
        * Load shops with prices. Inner join on shop and price tables.
        *
        * */
        loadShopsWithPricesFromRDS();
    }

    public void loadProductsFromRDS() {
        Log.i(TAG, "loadProductsFromRDS: running get products");
        String url = new Uri.Builder().scheme("https")
                .authority("s3459317-rmit.appspot.com")
                .appendPath("getproducts.php")
                .appendQueryParameter("userid", MainActivity.getmUserEmail()).build().toString();

        Log.i(TAG, "loadProductsFromRDS: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                final JSONArray results;

                try {
                    results = jsonObject.getJSONArray("products");

                    for (int i = 0; i < results.length(); i++) {
                        String productid = results.getJSONObject(i).getString("productid");
                        String name = results.getJSONObject(i).getString("name");
                        String description = results.getJSONObject(i).getString("description");
                        String imageurl = results.getJSONObject(i).getString("imageurl");

                        ContentResolver contentResolver = getContext().getContentResolver();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(ShopAlertContract.ProductEntry.COLUMN_PRODUCT_ID, productid);
                        contentValues.put(ShopAlertContract.ProductEntry.COLUMN_NAME, name);
                        contentValues.put(ShopAlertContract.ProductEntry.COLUMN_DESCRIPTION, description);
                        // No images available at this time so just using static placeholder
                        contentValues.put(ShopAlertContract.ProductEntry.COLUMN_IMAGE_URL, "https://s3.amazonaws.com/modernlook.com-cdn/products/0/0/0/400/446/s1/soapstone_pizza_stone_12_2215.jpg");
                        contentResolver.insert(ShopAlertContract.ProductEntry.CONTENT_URI, contentValues);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "onResponse: didn't parse products from sync", e);
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "onResponse: didn't get products during sync", volleyError);
            }
        });

        VolleyRequestQueue.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void loadShopsWithPricesFromRDS() {
        Log.i(TAG, "loadShopsWithPricesFromRDS: running get shopswithprices");
        String url = new Uri.Builder().scheme("https")
                .authority("s3459317-rmit.appspot.com")
                .appendPath("getshopswithprices.php")
                .appendQueryParameter("userid", MainActivity.getmUserEmail()).build().toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                final JSONArray results;

                try {
                    results = jsonObject.getJSONArray("shopswithprices");

                    Log.i(TAG, "onResponse: SIZE: " + results.length());
                    Log.i(TAG, "onResponse: name: " + results.getJSONObject(0).getString("name"));

                    for (int i = 0; i < results.length(); i++) {
                        String shopid = results.getJSONObject(i).getString("shopid");
                        String name = results.getJSONObject(i).getString("name");
                        String latitude = results.getJSONObject(i).getString("latitude");
                        String longitude = results.getJSONObject(i).getString("longitude");
                        String imageurl = results.getJSONObject(i).getString("imageurl");
                        String priceid = results.getJSONObject(i).getString("priceid");
                        String price = results.getJSONObject(i).getString("price");
                        String productId = results.getJSONObject(i).getString("productid");

                        ContentResolver contentResolver = getContext().getContentResolver();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(ShopAlertContract.ShopWithPriceEntry.COLUMN_SHOP_ID, shopid);
                        contentValues.put(ShopAlertContract.ShopWithPriceEntry.COLUMN_NAME, name);
                        contentValues.put(ShopAlertContract.ShopWithPriceEntry.COLUMN_LATITUDE, latitude);
                        contentValues.put(ShopAlertContract.ShopWithPriceEntry.COLUMN_LONGITUDE, longitude);
                        contentValues.put(ShopAlertContract.ShopWithPriceEntry.COLUMN_IMAGE_URL, imageurl);
                        contentValues.put(ShopAlertContract.ShopWithPriceEntry.COLUMN_PRICE_ID, priceid);
                        contentValues.put(ShopAlertContract.ShopWithPriceEntry.COLUMN_PRICE, price);
                        contentValues.put(ShopAlertContract.ShopWithPriceEntry.COLUMN_PRODUCT_ID, productId);
                        contentResolver.insert(ShopAlertContract.ShopWithPriceEntry.CONTENT_URI, contentValues);

                        // Add geofence
                        Log.i(TAG, "onResponse: added geo fence: " + latitude + ", " + longitude);
                        mGeofenceList.add(new Geofence.Builder().setRequestId(productId)
                                .setCircularRegion(Double.parseDouble(latitude), Double.parseDouble(longitude), Constants.GEOFENCE_RADIUS_IN_METERS)
                                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                                .setNotificationResponsiveness(0)
                                .build());


                        if (!mGoogleApiClient.isConnected()) {
                            return;
                        }

                        try {
                            LocationServices.GeofencingApi.addGeofences(
                                    mGoogleApiClient,
                                    // The GeofenceRequest object.
                                    getGeofencingRequest(),
                                    // A pending intent that that is reused when calling removeGeofences(). This
                                    // pending intent is used to generate an intent when a matched geofence
                                    // transition is observed.
                                    getGeofencePendingIntent()
                            ).setResultCallback(mStatusResultCallback); // Result processed in onResult().
                        } catch (SecurityException securityException) {
                            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
//                            logSecurityException(securityException);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(TAG, "onResponse: GOT AN ERROR: " + volleyError.toString());
            }
        });

        VolleyRequestQueue.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(getContext(), GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onResult(Status status) {
        Log.i(TAG, "onResult: " + status.toString());
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            return null;
        }

        return newAccount;
    }
}
