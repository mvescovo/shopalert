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

package com.shopalert.app.addproduct;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shopalert.app.Injection;
import com.shopalert.app.shopalert.R;

/**
 * Created by Michael Vescovo on 6/02/16.
 *
 */
public class AddProductFragment extends Fragment implements AddProductContract.View {

    private AddProductContract.UserActionsListener mActionListener;
    private TextView mName;
    private TextView mDescription;

    public static AddProductFragment newInstance() {
        return new AddProductFragment();
    }

    public AddProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActionListener = new AddProductPresenter(Injection.provideShopAlertRepository(), this);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_done_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionListener.saveProduct(getActivity(), mName.getText().toString(), mDescription.getText().toString());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_product, container, false);
        mName = (TextView) root.findViewById(R.id.add_product_name);
        mDescription = (TextView) root.findViewById(R.id.add_product_description);
        return root;
    }

    @Override
    public void showEmptyProductError() {
        Snackbar.make(mName, getString(R.string.empty_product_message), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showProductsList() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void setUserActionListener(AddProductContract.UserActionsListener listener) {
        mActionListener = listener;
    }
}
