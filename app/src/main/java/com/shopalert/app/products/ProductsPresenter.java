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

package com.shopalert.app.products;

import android.content.Context;
import android.support.annotation.NonNull;

import com.shopalert.app.data.Product;
import com.shopalert.app.data.ShopAlertRepository;
import com.shopalert.app.util.EspressoIdlingResource;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Michael Vescovo on 2/02/16.
 *
 */
public class ProductsPresenter implements ProductsContract.UserActionsListener {

    private final ShopAlertRepository mShopAlertRepository;
    private final ProductsContract.View mProductsView;

    public ProductsPresenter(@NonNull ShopAlertRepository shopAlertRepository, @NonNull com.shopalert.app.products.ProductsContract.View productsView) {
        mShopAlertRepository = checkNotNull(shopAlertRepository, "shopAlertRepository cannot be null");
        mProductsView = checkNotNull(productsView, "viewItemsView cannot be null");
    }

    @Override
    public void loadProducts(Context context, boolean forceUpdate) {
        mProductsView.setProgressIndicator(true);
        if (forceUpdate) {
            mShopAlertRepository.refreshProductData();
        }

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        mShopAlertRepository.getProducts(context, new ShopAlertRepository.LoadProductsCallback() {
            @Override
            public void onProductsLoaded(List<Product> products) {
                EspressoIdlingResource.decrement(); // Set app as idle.
                mProductsView.setProgressIndicator(false);
                mProductsView.showProducts(products);
            }
        });
    }

    @Override
    public void addNewProduct() {
        mProductsView.showAddProduct();
    }

    @Override
    public void openProduct(@NonNull Product requestedProduct) {
        checkNotNull(requestedProduct, "requestedProduct cannot be null");
        mProductsView.showPricesUi(requestedProduct.getProductId());
    }
}
