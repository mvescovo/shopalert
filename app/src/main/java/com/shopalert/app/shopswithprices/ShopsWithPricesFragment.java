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

package com.shopalert.app.shopswithprices;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shopalert.app.Injection;
import com.shopalert.app.data.ShopWithPrice;
import com.shopalert.app.shopalert.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Michael Vescovo on 3/02/16.
 *
 */
public class ShopsWithPricesFragment extends Fragment implements ShopsWithPricesContract.View, LoaderManager.LoaderCallbacks {

    private static final String TAG = "ShopsWithPricesFragment";

    public static final String ARGUMENT_PRODUCT_ID = "PRODUCT_ID";
    private static final int REQUEST_ADD_SHOP_WITH_PRICE = 1;
    private ShopsWithPricesContract.UserActionsListener mActionsListener;
    private ShopsWithPricesAdapter mShopsWithPricesAdapter;

    public ShopsWithPricesFragment() {
        // Requires empty public constructor
    }

    public static ShopsWithPricesFragment newInstance(String productId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_PRODUCT_ID, productId);
        ShopsWithPricesFragment fragment = new ShopsWithPricesFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShopsWithPricesAdapter = new ShopsWithPricesAdapter(getContext(), new ArrayList<ShopWithPrice>(0), mItemListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shops_with_prices, container, false);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.shops_with_prices_list);
        recyclerView.setAdapter(mShopsWithPricesAdapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Pull-to-refresh
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String productId = getArguments().getString(ARGUMENT_PRODUCT_ID);
                mActionsListener.loadShopsWithPrices(getContext(), productId, true);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);

        mActionsListener = new ShopsWithPricesPresenter(Injection.provideShopAlertRepository(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
        String productId = getArguments().getString(ARGUMENT_PRODUCT_ID);
        mActionsListener.loadShopsWithPrices(getContext(), productId, true);
    }

    /**
     * Listener for clicks on items in the RecyclerView.
     */
    ItemListener mItemListener = new ItemListener() {
        @Override
        public void onItemClick(ShopWithPrice clickedShopWithPrice) {
            mActionsListener.openShopWithPrice(getContext(), clickedShopWithPrice);
        }
    };

    @Override
    public void setProgressIndicator(final boolean active) {

        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl = (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);

        // Make sure setRefreshing() is called after the layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showShopsWithPrices(List<ShopWithPrice> shopsWithPrices) {
        mShopsWithPricesAdapter.replaceData(shopsWithPrices);
    }

    @Override
    public void showDirectionsUi(String priceId) {
        Intent intent = new Intent(getContext(), ShopsWithPricesActivity.class);
        intent.putExtra(ShopsWithPricesActivity.EXTRA_PRODUCT_ID, priceId);
        startActivity(intent);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private static class ShopsWithPricesAdapter extends RecyclerView.Adapter<ShopsWithPricesAdapter.ViewHolder> {

        private Context mContext;
        private List<ShopWithPrice> mShopsWithPrices;
        private ItemListener mItemListener;

        public ShopsWithPricesAdapter(Context context, List<ShopWithPrice> shopsWithPrices, ItemListener itemListener) {
            mContext = context;
            setShopsWithPricesList(shopsWithPrices);
            mItemListener = itemListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View itemView = inflater.inflate(R.layout.shop_with_price, parent, false);

            return new ViewHolder(itemView, mItemListener);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            final ShopWithPrice shopWithPrice = mShopsWithPrices.get(position);

            viewHolder.price.setText(shopWithPrice.getPrice());
            viewHolder.name.setText(shopWithPrice.getName());
            Picasso.with(mContext).load(shopWithPrice.getImageUrl()).into(viewHolder.imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                }
            });
        }

        public void replaceData(List<ShopWithPrice> shopsWithPrices) {
            setShopsWithPricesList(shopsWithPrices);
            notifyDataSetChanged();
        }

        private void setShopsWithPricesList(List<ShopWithPrice> shopsWithPricesList) {
            mShopsWithPrices = checkNotNull(shopsWithPricesList);
        }

        @Override
        public int getItemCount() {
            Log.i(TAG, "getItemCount: " + mShopsWithPrices.size());
            return mShopsWithPrices.size();
        }

        public ShopWithPrice getShopWithPrice(int position) {
            return mShopsWithPrices.get(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView imageView;
            public TextView price;
            public TextView name;
            private ItemListener mItemListener;

            public ViewHolder(View shopWithPriceView, ItemListener listener) {
                super(shopWithPriceView);
                mItemListener = listener;
                imageView = (ImageView) shopWithPriceView.findViewById(R.id.street_view_image);
                price = (TextView) shopWithPriceView.findViewById(R.id.price);
                name = (TextView) shopWithPriceView.findViewById(R.id.name);
                shopWithPriceView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                ShopWithPrice shopWithPrice = getShopWithPrice(position);
                mItemListener.onItemClick(shopWithPrice);

            }
        }
    }

    public interface ItemListener {

        void onItemClick(ShopWithPrice clickedShopWithPrice);
    }
}
