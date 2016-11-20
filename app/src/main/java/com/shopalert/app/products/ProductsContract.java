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

import java.util.List;

/**
 * Created by Michael Vescovo on 2/02/16.
 *
 */
public interface ProductsContract {

    interface View {

        void setProgressIndicator(boolean active);

        void showProducts(List<Product> products);

        void showAddProduct();

        void showPricesUi(String productId);
    }

    interface UserActionsListener {

        void loadProducts(@NonNull Context context, boolean forceUpdate);

        void addNewProduct();

        void openProduct(@NonNull Product requestedProduct);
    }
}
