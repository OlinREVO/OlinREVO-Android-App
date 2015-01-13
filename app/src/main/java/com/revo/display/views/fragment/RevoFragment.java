package com.revo.display.views.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;

import com.revo.display.RevoApplication;

/**
 * Created by sihrc on 9/20/14.
 */
public abstract class RevoFragment extends Fragment {
    public static final String DIRECTION_KEY = "com_revo_display_views_fragment_direction";
    private static String SHARED_PREFS_KEY = "com.revo.display.views.fragment.shared_prefs";
    private static String IS_DRIVER_KEY = "com.revo.display.views.fragment.is_driver";

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
