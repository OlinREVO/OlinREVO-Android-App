package com.revo.display.views.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sihrc on 9/20/14.
 */
public abstract class RevoFragment extends Fragment {
    public static final String DIRECTION_KEY = "com_revo_display_views_fragment_direction";
    private static String SHARED_PREFS_KEY = "com.revo.display.views.fragment.shared_prefs";
    private static String IS_DRIVER_KEY = "com.revo.display.views.fragment.is_driver";

    public abstract String tag();

    public boolean isDriver() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        return true; //sharedPrefs.getBoolean(IS_DRIVER_KEY, false);
    }
}
