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

import android.support.v4.util.ArrayMap;

/**
 * Created by Michael Vescovo on 6/02/16.
 *
 */
public class ShopAlertServiceApiEndpoint {

    static {
        PRODUCT_DATA = new ArrayMap(3);
        addProduct("Nexus 9", "9 inch Nexus Tablet from HTC.", "https://www.google.com/nexus/images/nexus9/N9-grid1-1600.jpg", "1");
        addProduct("Dell XPS 13", "13 inch Dell laptop.", "http://s3.amazonaws.com/digitaltrends-uploads-prod/2015/05/Dell-XPS-13-Ubuntu.jpg", "2");
        addProduct("Pizza stone", "For making pizzas at home", "https://s3.amazonaws.com/modernlook.com-cdn/products/0/0/0/400/446/s1/soapstone_pizza_stone_12_2215.jpg", "3");

        SHOP_WITH_PRICE_DATA = new ArrayMap(3);
        addShopWithPrice("Jb hifi", "-37.808314", "144.961814", "https://geo0.ggpht.com/cbk?panoid=j2vyI7aI3NfAephIR9Of0A&output=thumbnail&cb_client=search.TACTILE.gps&thumb=2&w=408&h=256&yaw=69.688454&pitch=0", "$24.95", "1");
        addShopWithPrice("Dick smith", "-37.813173", "145.011840", "https://geo2.ggpht.com/cbk?panoid=7S8sZrKszrzrb8KKwo0k1g&output=thumbnail&cb_client=search.TACTILE.gps&thumb=2&w=408&h=256&yaw=308.16513&pitch=0", "$20.00", "2");
        addShopWithPrice("Officeworks", "-37.819469", "145.010799", "https://geo3.ggpht.com/cbk?panoid=MUcZvKIs6c2-JOawEIQusA&output=thumbnail&cb_client=search.TACTILE.gps&thumb=2&w=408&h=256&yaw=166.87926&pitch=0", "$29.95", "1");
    }

    private final static ArrayMap<String, Product> PRODUCT_DATA;
    private final static ArrayMap<String, ShopWithPrice> SHOP_WITH_PRICE_DATA;

    private static void addProduct(String name, String description, String imageUrl, String productId) {
        Product newProduct = new Product(name, description, imageUrl, productId);
        PRODUCT_DATA.put(newProduct.getProductId(), newProduct);
    }

    private static void addShopWithPrice(String name, String latitude, String longitude, String imageUrl, String price, String productId) {
        ShopWithPrice newShopWithPrice = new ShopWithPrice(name, latitude, longitude, imageUrl, price, productId);
        SHOP_WITH_PRICE_DATA.put(newShopWithPrice.getShopWithPriceId(), newShopWithPrice);
    }

    public static ArrayMap<String, Product> loadPersistedProducts() {
        return PRODUCT_DATA;
    }

    public static ArrayMap<String, ShopWithPrice> loadPersistedShopsWithPrices() {
        return SHOP_WITH_PRICE_DATA;
    }
}
