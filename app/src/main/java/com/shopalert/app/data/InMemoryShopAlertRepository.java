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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.common.collect.ImmutableList;
import com.shopalert.app.sync.ShopAlertSyncAdapter;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Michael Vescovo on 2/02/16.
 *
 */
public class InMemoryShopAlertRepository implements ShopAlertRepository {

    private static final String TAG = "InMemoryShopAlertRepository";

    private final ShopAlertServiceApi mShopAlertServiceApi;
//    Account mAccount;
    Context mContext;

    /**
     * This method has reduced visibility for testing and is only visible to tests in the same
     * package.
     */
    @VisibleForTesting
    List<Product> mCachedProducts;

    @VisibleForTesting
    List<ShopWithPrice> mCachedShopsWithPrices;

    public InMemoryShopAlertRepository(@NonNull ShopAlertServiceApi shopAlertServiceApi) {
        mShopAlertServiceApi = checkNotNull(shopAlertServiceApi);
    }

    @Override
    public void getProducts(@NonNull Context context, @NonNull final LoadProductsCallback callback) {
        checkNotNull(callback);
        mContext = context;
        // Load from API only if needed.
        if (mCachedProducts == null) {
            mShopAlertServiceApi.getAllProducts(context, new ShopAlertServiceApi.ProductServiceCallback<List<Product>>() {
                @Override
                public void onLoaded(List<Product> products) {
                    mCachedProducts = ImmutableList.copyOf(products);
                    callback.onProductsLoaded(mCachedProducts);
                }
            });
        } else {
            callback.onProductsLoaded(mCachedProducts);
        }
    }

    @Override
    public void saveProduct(@NonNull Context context, @NonNull Product product) {
        mContext = context;
        checkNotNull(product);
        mShopAlertServiceApi.saveProduct(context, product);
        refreshProductData();
    }

    @Override
    public void refreshProductData() {
        // Perform sync
        ShopAlertSyncAdapter.syncImmediately(mContext);
//        mAccount = CreateSyncAccount(mContext);
//        Bundle settingsBundle = new Bundle();
//        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
//        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
//        ContentResolver.requestSync(mAccount, ShopAlertContract.CONTENT_AUTHORITY, settingsBundle);
        mCachedProducts = null;
    }

    @Override
    public void getShopsWithPrices(Context context, String productId, @NonNull final LoadShopsWithPricesCallback callback) {
        checkNotNull(callback);
        mContext = context;
        // Load from API only if needed.
        if (mCachedShopsWithPrices == null) {
            mShopAlertServiceApi.getAllShopsWithPrices(mContext, productId, new ShopAlertServiceApi.ShopWithPriceServiceCallback<List<ShopWithPrice>>() {
                @Override
                public void onLoaded(List<ShopWithPrice> shopsWithPrices) {
                    mCachedShopsWithPrices = ImmutableList.copyOf(shopsWithPrices);
                    callback.onShopsWithPricesLoaded(mCachedShopsWithPrices);
                }
            });
        } else {
            callback.onShopsWithPricesLoaded(mCachedShopsWithPrices);
        }
    }

    @Override
    public void refreshShopWithPriceData() {
        // Perform sync
        ShopAlertSyncAdapter.syncImmediately(mContext);
//        mAccount = CreateSyncAccount(mContext);
//        Bundle settingsBundle = new Bundle();
//        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
//        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
//        ContentResolver.requestSync(mAccount, ShopAlertContract.CONTENT_AUTHORITY, settingsBundle);
        mCachedShopsWithPrices = null;
    }

//    public static Account CreateSyncAccount(Context context) {
//        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
//        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
//
//        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
//            return null;
//        }
//
//        return newAccount;
//    }
}
