package com.revo.display.views.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.view.View;

import com.revo.display.RevoApplication;
import com.revo.display.bluetooth.DataReceiver;
import com.revo.display.bluetooth.ValuesCallback;

/**
 * Created by sihrc on 9/20/14.
 */
public abstract class RevoFragment extends Fragment {
    Activity activity;
    boolean registered = false;
    BroadcastReceiver receiver;

    public void updateMode() {
        if (RevoApplication.isDriver)
            setupDriverMode();
        else
            setupNotDriverMode();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        receiver = new DataReceiver(getValuesCallback());
        updateMode();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMode();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (activity != null && receiver != null && registered) {
            activity.unregisterReceiver(receiver);
            registered = false;
        }
    }

    public abstract String tag();
    public abstract void setupDriverMode();
    public abstract void setupNotDriverMode();
    public abstract ValuesCallback getValuesCallback();
}
