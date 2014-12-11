package com.revo.display.views.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.revo.display.R;
import com.revo.display.RevoApplication;
import com.revo.display.bluetooth.BLEActivity;

/**
 * Created by sihrc on 9/20/14.
 */
public class DeveloperFragment extends RevoFragment {
    BLEActivity activity;

    @Override
    public void onAttach(Activity activity) {
        this.activity = (BLEActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.developer_fragment, container);

        rootView.findViewById(R.id.read_characteristic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                DeveloperFragment.this.activity.readCharacteristic();
            }
        });

        return rootView;
    }

    @Override
    public String tag() {
        return DeveloperFragment.class.getSimpleName();
    }
}
