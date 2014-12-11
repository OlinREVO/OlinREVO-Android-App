package com.revo.display.views.fragment;

import android.app.Fragment;

import com.revo.display.RevoApplication;

/**
 * Created by sihrc on 9/20/14.
 */
public abstract class RevoFragment extends Fragment {
    public abstract String tag();
    public abstract void setupDriverMode();
    public abstract void setupNotDriverMode();
    public void updateMode() {
        if (RevoApplication.isDriver)
            setupDriverMode();
        else
            setupNotDriverMode();
    }
}
