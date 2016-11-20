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
import android.support.annotation.NonNull;

import com.shopalert.app.data.ShopWithPrice;

import java.util.List;

/**
 * Created by Michael Vescovo on 3/02/16.
 *
 */
public interface ShopsWithPricesContract {

    interface View {

        void setProgressIndicator(boolean active);

        void showShopsWithPrices(List<ShopWithPrice> shopWithPrices);

        void showDirectionsUi(String shopWithpriceId);
    }

    interface UserActionsListener {

        void loadShopsWithPrices(Context context, String productId, boolean forceUpdate);

        void openShopWithPrice(Context context, @NonNull ShopWithPrice requestedShopWithPrice);
    }
}
