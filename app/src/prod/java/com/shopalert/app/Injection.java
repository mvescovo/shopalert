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

package com.shopalert.app;

import com.shopalert.app.data.ShopAlertRepositories;
import com.shopalert.app.data.ShopAlertRepository;
import com.shopalert.app.data.ShopAlertServiceApiImpl;

/**
 * Created by Michael Vescovo on 6/02/16.
 *
 */
public class Injection {

    public static ShopAlertRepository provideShopAlertRepository() {
        return ShopAlertRepositories.getInMemoryRepoInstance(new ShopAlertServiceApiImpl());
    }

}
