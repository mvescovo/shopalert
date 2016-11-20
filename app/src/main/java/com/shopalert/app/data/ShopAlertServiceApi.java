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

import java.util.List;

/**
 * Created by Michael Vescovo on 2/02/16.
 *
 * Defines an interface to the service API that is used by this application. All data requests should
 * be piped through this interface.
 */
public interface ShopAlertServiceApi {

    interface ProductServiceCallback<T> {

        void onLoaded(T products);
    }
    void getAllProducts(Context context, ProductServiceCallback<List<Product>> callback);


    interface ShopWithPriceServiceCallback<T> {

        void onLoaded(T shopsWithPrices);
    }
    void getAllShopsWithPrices(Context context, String productId, ShopWithPriceServiceCallback<List<ShopWithPrice>> callback);


    void saveProduct(Context context, Product product);
}
