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
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.ArrayMap;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael Vescovo on 2/02/16.
 *
 */
public class FakeShopAlertServiceApiImpl implements ShopAlertServiceApi {

    private static final ArrayMap<String, Product> PRODUCT_SERVICE_DATA = new ArrayMap();
    static {
        Product product1 = new Product("Product 1");
        Product product2 = new Product("Nexus 9", "9 inch Nexus Tablet from HTC.", "https://www.google.com/nexus/images/nexus9/N9-grid1-1600.jpg", "1");
        Product product3 = new Product("Product 3");
        Product product4 = new Product("Dell XPS 13", "13 inch Dell laptop.", "http://s3.amazonaws.com/digitaltrends-uploads-prod/2015/05/Dell-XPS-13-Ubuntu.jpg", "2");
        Product product5 = new Product("Pizza stone", "", "https://s3.amazonaws.com/modernlook.com-cdn/products/0/0/0/400/446/s1/soapstone_pizza_stone_12_2215.jpg", "3");
        PRODUCT_SERVICE_DATA.put(product1.getProductId(), product1);
        PRODUCT_SERVICE_DATA.put(product2.getProductId(), product2);
        PRODUCT_SERVICE_DATA.put(product3.getProductId(), product3);
        PRODUCT_SERVICE_DATA.put(product4.getProductId(), product4);
        PRODUCT_SERVICE_DATA.put(product5.getProductId(), product5);
    }

    private static final ArrayMap<String, ShopWithPrice> SHOP_WITH_PRICE_SERVICE_DATA = new ArrayMap();
    static {
        ShopWithPrice shopWithPrice1 = new ShopWithPrice("Jb hifi", "-37.808314", "144.961814", "https://geo0.ggpht.com/cbk?panoid=j2vyI7aI3NfAephIR9Of0A&output=thumbnail&cb_client=search.TACTILE.gps&thumb=2&w=408&h=256&yaw=69.688454&pitch=0", "$24.95", "1");
        ShopWithPrice shopWithPrice2 = new ShopWithPrice("Dick smith", "-37.813173", "145.011840", "https://geo2.ggpht.com/cbk?panoid=7S8sZrKszrzrb8KKwo0k1g&output=thumbnail&cb_client=search.TACTILE.gps&thumb=2&w=408&h=256&yaw=308.16513&pitch=0", "$20.00", "2");
        ShopWithPrice shopWithPrice3 = new ShopWithPrice("Officeworks", "-37.819469", "145.010799", "https://geo3.ggpht.com/cbk?panoid=MUcZvKIs6c2-JOawEIQusA&output=thumbnail&cb_client=search.TACTILE.gps&thumb=2&w=408&h=256&yaw=166.87926&pitch=0", "$29.95", "1");
        SHOP_WITH_PRICE_SERVICE_DATA.put(shopWithPrice1.getPriceId(), shopWithPrice1);
        SHOP_WITH_PRICE_SERVICE_DATA.put(shopWithPrice2.getPriceId(), shopWithPrice2);
        SHOP_WITH_PRICE_SERVICE_DATA.put(shopWithPrice3.getPriceId(), shopWithPrice3);
    }

    @Override
    public void getAllProducts(Context context, ShopAlertServiceApi.ProductServiceCallback<List<Product>> callback) {
        callback.onLoaded(Lists.newArrayList(PRODUCT_SERVICE_DATA.values()));
    }

    @Override
    public void getProduct(String productId, ProductServiceCallback<Product> callback) {
        Product product = PRODUCT_SERVICE_DATA.get(productId);
        callback.onLoaded(product);
    }

    @Override
    public void saveProduct(Context context, Product product) {
        PRODUCT_SERVICE_DATA.put(product.getProductId(), product);
    }

    @VisibleForTesting
    public static void addProducts(Product... products) {
        for (Product product : products) {
            PRODUCT_SERVICE_DATA.put(product.getProductId(), product);
        }
    }

    @Override
    public void getAllShopsWithPrices(String productId, ShopWithPriceServiceCallback<List<ShopWithPrice>> callback) {
        List<ShopWithPrice> tempShopsWithPrices = new ArrayList<>(SHOP_WITH_PRICE_SERVICE_DATA.values());
        List<ShopWithPrice> shopsWithPrices = new ArrayList<>();

        for (int i = 0; i < tempShopsWithPrices.size(); i++) {
            if (tempShopsWithPrices.get(i).getProductId().contentEquals(productId)) {
                shopsWithPrices.add(tempShopsWithPrices.get(i));
            }
        }

        callback.onLoaded(shopsWithPrices);
    }

    @Override
    public void getShopWithPrice(String shopWithPriceId, ShopWithPriceServiceCallback<ShopWithPrice> callback) {

    }

    @Override
    public void saveShopWithPrice(ShopWithPrice shopWithPrice) {

    }
}
