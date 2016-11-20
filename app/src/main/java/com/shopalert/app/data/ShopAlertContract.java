package com.shopalert.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Michael Vescovo on 6/02/16.
 *
 * Defines table and column names for the ShopAlert database.
 *
 */
public class ShopAlertContract {

    public static final String CONTENT_AUTHORITY = "com.shopalert.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRODUCT = "product";
    public static final String PATH_SHOP_WITH_PRICE = "shop_with_price";
    public static final String PATH_USER_PRODUCT = "user_product";

    /* Inner class that defines the table contents of the product table */
    public static final class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCT).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        public static final String TABLE_NAME = "product";

        public static final String COLUMN_PRODUCT_ID = "product_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION= "description";
        public static final String COLUMN_IMAGE_URL = "image_url";

        public static Uri buildProductUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildProductUser(String user) {
            return CONTENT_URI.buildUpon().appendPath(user).build();
        }
    }

    /* Inner class that defines the table contents of the shop with price table */
    public static final class ShopWithPriceEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SHOP_WITH_PRICE).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHOP_WITH_PRICE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHOP_WITH_PRICE;

        public static final String TABLE_NAME = "shop_with_price";

        public static final String COLUMN_SHOP_ID = "shop_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_PRICE_ID = "price_id";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_PRODUCT_ID = "product_id";

        public static Uri buildShopWithPriceUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the userproduct table */
    public static final class UserProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER_PRODUCT).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER_PRODUCT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER_PRODUCT;

        public static final String TABLE_NAME = "user_product";

        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_PRODUCT_ID = "product_id";

        public static Uri buildUserProductUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
