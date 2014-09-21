package com.revo.display.views.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.revo.display.R;

/**
 * Created by sihrc on 9/20/14.
 */
public class DeveloperFragment extends RevoFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.developer_fragment, container);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public String tag() {
        return DeveloperFragment.class.getSimpleName();
    }
}
