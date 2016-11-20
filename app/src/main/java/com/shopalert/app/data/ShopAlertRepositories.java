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

import android.support.annotation.NonNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Michael Vescovo on 2/02/16.
 *
 */
public class ShopAlertRepositories {

    private ShopAlertRepositories() {
        // no instance
    }

    private static ShopAlertRepository repository = null;

    public synchronized static ShopAlertRepository getInMemoryRepoInstance(@NonNull ShopAlertServiceApi shopAlertServiceApi) {
        checkNotNull(shopAlertServiceApi);
        if (null == repository) {
            repository = new InMemoryShopAlertRepository(shopAlertServiceApi);
        } else {
        }
        return repository;
    }
}
