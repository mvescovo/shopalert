package com.shopalert.app.data;

import android.support.annotation.Nullable;

/**
 * Created by Michael Vescovo on 2/02/16.
 *
 */
public final class Product {

    @Nullable
    private String mProductId;
    private String mName;
    @Nullable
    private String mDescription;
    @Nullable
    private String mImageUrl;

    public Product(String name) {
        this(name, null, null, null);
    }

    public Product(String name, @Nullable String description) {
        this(name, description, null, null);
    }

    public Product(String name, @Nullable String description, @Nullable String imageUrl, @Nullable String productId) {
        setName(name);
        setDescription(description);
        setImageUrl(imageUrl);
        setProductId(productId);
    }

    public String getProductId() {
        return mProductId;
    }

    public void setProductId(String itemId) {
        mProductId = itemId;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setDescription(@Nullable String description) {
        mDescription = description;
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public boolean isEmpty() {
        return (mName == null || "".equals(mName)) &&
                (mDescription == null || "".equals(mDescription));
    }
}
