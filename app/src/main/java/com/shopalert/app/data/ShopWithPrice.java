package com.shopalert.app.data;

import android.support.annotation.Nullable;

import java.util.UUID;

/**
 * Created by Michael Vescovo on 4/02/16.
 *
 */
public class ShopWithPrice {

    private String mShopWithPriceId;
    private String mShopId;
    private String mName;
    private String mLatitude;
    private String mLongitude;
    private String mImageUrl;
    private String mPriceId;
    private String mPrice;
    private String mProductId;

    public ShopWithPrice(String name, String latitude, String longitude, @Nullable String imageUrl, String price, String productId) {
        setShopWithPriceId(UUID.randomUUID().toString());
        setShopId(UUID.randomUUID().toString());
        setName(name);
        setLatitude(latitude);
        setLongitude(longitude);
        setImageUrl(imageUrl);
        setPriceId(UUID.randomUUID().toString());
        setPrice(price);
        setProductId(productId);
    }

    public String getShopWithPriceId() {
        return mShopWithPriceId;
    }

    public void setShopWithPriceId(String shopWithPriceId) {
        mShopWithPriceId = shopWithPriceId;
    }

    public String getShopId() {
        return mShopId;
    }

    public void setShopId(String shopId) {
        mShopId = shopId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public void setLatitude(String latitude) {
        mLatitude = latitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public void setLongitude(String longitude) {
        mLongitude = longitude;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getPriceId() {
        return mPriceId;
    }

    public void setPriceId(String priceId) {
        mPriceId = priceId;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    public String getProductId() {
        return mProductId;
    }

    public void setProductId(String productId) {
        mProductId = productId;
    }
}
