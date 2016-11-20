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

package com.shopalert.app.addproduct;

import android.content.Context;
import android.support.annotation.NonNull;

import com.shopalert.app.data.Product;
import com.shopalert.app.data.ShopAlertRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Michael Vescovo on 6/02/16.
 *
 */
public class AddProductPresenter implements AddProductContract.UserActionsListener {

    @NonNull
    private final ShopAlertRepository mShopAlertRepository;

    @NonNull
    private final AddProductContract.View mAddProductView;

    public AddProductPresenter(@NonNull ShopAlertRepository shopAlertRepository, @NonNull AddProductContract.View addProductView) {
        mShopAlertRepository = checkNotNull(shopAlertRepository);
        mAddProductView = checkNotNull(addProductView);
        addProductView.setUserActionListener(this);
    }

    @Override
    public void saveProduct(Context context, String name, String description) {
        Product newProduct = new Product(name, description);
        if (newProduct.isEmpty()) {
            mAddProductView.showEmptyProductError();
        } else {
            mShopAlertRepository.saveProduct(context, newProduct);
            mAddProductView.showProductsList();
        }
    }
}
