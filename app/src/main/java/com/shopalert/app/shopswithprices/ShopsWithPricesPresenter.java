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

package com.shopalert.app.shopswithprices;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.shopalert.app.data.ShopAlertRepository;
import com.shopalert.app.data.ShopWithPrice;
import com.shopalert.app.util.EspressoIdlingResource;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Michael Vescovo on 3/02/16.
 *
 */
public class ShopsWithPricesPresenter implements ShopsWithPricesContract.UserActionsListener {
    private static final String TAG = "ShopsWithPricesPresenter";

    private final ShopAlertRepository mShopAlertRepository;
    private final ShopsWithPricesContract.View mShopsWithPricesView;

    public ShopsWithPricesPresenter(@NonNull ShopAlertRepository shopAlertRepository, @NonNull ShopsWithPricesContract.View shopsWithPricesView) {
        mShopAlertRepository = checkNotNull(shopAlertRepository, "shopAlertRepository cannot be null");
        mShopsWithPricesView = checkNotNull(shopsWithPricesView, "shopsWithPricesView cannot be null");
    }

    @Override
    public void loadShopsWithPrices(Context context, String productId, boolean forceUpdate) {
        mShopsWithPricesView.setProgressIndicator(true);
        if (forceUpdate) {
            mShopAlertRepository.refreshShopWithPriceData();
        }

         //The network request might be handled in a different thread so make sure Espresso knows
         //that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        mShopAlertRepository.getShopsWithPrices(context, productId, new ShopAlertRepository.LoadShopsWithPricesCallback() {
            @Override
            public void onShopsWithPricesLoaded(List<ShopWithPrice> shopsWithPrices) {
                EspressoIdlingResource.decrement(); // Set app as idle.
                mShopsWithPricesView.setProgressIndicator(false);
                if (shopsWithPrices.size() > 0) {
                    mShopsWithPricesView.showShopsWithPrices(shopsWithPrices);
                }
            }
        });
    }

    @Override
    public void openShopWithPrice(Context context, @NonNull ShopWithPrice requestedShopWithPrice) {
//        String uri = "geo:0,0?q="+ requestedShopWithPrice.getLatitude() + "," + requestedShopWithPrice.getLongitude() + " (" + requestedShopWithPrice.getName()+ ")";
//        context.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));

        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + requestedShopWithPrice.getLatitude() + ", " + requestedShopWithPrice.getLongitude() + "(" + requestedShopWithPrice.getName() + ")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }
}
