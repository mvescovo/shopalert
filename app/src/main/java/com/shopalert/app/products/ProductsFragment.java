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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shopalert.app.addproduct.AddProductActivity;
import com.shopalert.app.Injection;
import com.shopalert.app.data.Product;
import com.shopalert.app.data.ShopAlertContract;
import com.shopalert.app.shopalert.R;
import com.shopalert.app.shopswithprices.ShopsWithPricesActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Michael Vescovo on 2/02/16.
 *
 */
public class ProductsFragment extends Fragment implements ProductsContract.View, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int REQUEST_ADD_PRODUCT = 1;
    private ProductsContract.UserActionsListener mActionsListener;
    private ProductsAdapter mProductsAdapter;
    private static final int PRODUCTS_LOADER = 0;


    public ProductsFragment() {
        // Requires empty public constructor
    }

    public static ProductsFragment newInstance() {
        return new ProductsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProductsAdapter = new ProductsAdapter(new ArrayList<Product>(0), mItemListener, getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        mActionsListener.loadProducts(getContext(), false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);
        mActionsListener = new ProductsPresenter(Injection.provideShopAlertRepository(), this);
//        getLoaderManager().initLoader(PRODUCTS_LOADER, null, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If a product was successfully added, show snackbar
        if (REQUEST_ADD_PRODUCT == requestCode && Activity.RESULT_OK == resultCode) {
            Snackbar.make(getView(), getString(R.string.successfully_saved_product), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_products, container, false);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.products_list);
        recyclerView.setAdapter(mProductsAdapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_add_product);

        fab.setImageResource(R.drawable.ic_add_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionsListener.addNewProduct();
            }
        });

        // Pull-to-refresh
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mActionsListener.loadProducts(getContext(), true);
            }
        });
        return root;
    }

    /**
     * Listener for clicks on items in the RecyclerView.
     */
    ItemListener mItemListener = new ItemListener() {
        @Override
        public void onItemClick(Product clickedProduct) {
            mActionsListener.openProduct(clickedProduct);
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
    public void showProducts(List<Product> products) {
        mProductsAdapter.replaceData(products);
    }

    @Override
    public void showAddProduct() {
        Intent intent = new Intent(getContext(), AddProductActivity.class);
        startActivityForResult(intent, REQUEST_ADD_PRODUCT);
    }

    @Override
    public void showPricesUi(String productId) {
        Intent intent = new Intent(getContext(), ShopsWithPricesActivity.class);
        intent.putExtra(ShopsWithPricesActivity.EXTRA_PRODUCT_ID, productId);
        startActivity(intent);
    }

    private static class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

        private Context mContext;
        private List<Product> mProducts;
        private ItemListener mItemListener;
        private static Cursor mCursor = null;


        public ProductsAdapter(List<Product> products, ItemListener itemListener, Context context) {
            mContext = context;
            setList(products);
            mItemListener = itemListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View itemView = inflater.inflate(R.layout.product, parent, false);

            return new ViewHolder(itemView, mItemListener);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            final Product product = mProducts.get(position);

            viewHolder.name.setText(product.getName());
            viewHolder.description.setText(product.getDescription());
            Picasso.with(mContext).load(product.getImageUrl()).into(viewHolder.imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                }
            });


        }

        public void replaceData(List<Product> products) {
            setList(products);
            notifyDataSetChanged();
        }

        public void updateDataset(Cursor cursor) {
            mCursor = cursor;
            notifyDataSetChanged();
        }

        private void setList(List<Product> products) {
            mProducts = checkNotNull(products);
        }

        @Override
        public int getItemCount() {
            return mProducts.size();
        }

        public Product getProduct(int position) {
            return mProducts.get(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView name;
            public TextView description;
            public ImageView imageView;
            private ItemListener mItemListener;

            public ViewHolder(View productView, ItemListener listener) {
                super(productView);
                mItemListener = listener;
                name = (TextView) productView.findViewById(R.id.product_name);
                description = (TextView) productView.findViewById(R.id.product_description);
                imageView = (ImageView) productView.findViewById(R.id.product_image);
                productView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Product product = getProduct(position);
                mItemListener.onItemClick(product);
            }
        }
    }

    public interface ItemListener {

        void onItemClick(Product clickedProduct);
    }

    /*
    * Loader callbacks
    *
    * */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                ShopAlertContract.ProductEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mProductsAdapter.updateDataset(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductsAdapter.updateDataset(null);
    }
}
