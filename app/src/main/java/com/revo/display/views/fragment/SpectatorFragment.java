package com.revo.display.views.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.revo.display.R;
import com.revo.display.bluetooth.ValuesCallback;

/**
 * Created by sihrc on 9/20/14.
 */
public class SpectatorFragment extends RevoFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.spectator_fragment, container, false);
        return rootView;
    }

    @Override
    public String tag() {
        return SpectatorFragment.class.getSimpleName();
    }

    @Override
    public void setupDriverMode() {

    }

    @Override
    public void setupNotDriverMode() {

    }

    @Override
    public ValuesCallback getValuesCallback() {
        return null;
    }
}
