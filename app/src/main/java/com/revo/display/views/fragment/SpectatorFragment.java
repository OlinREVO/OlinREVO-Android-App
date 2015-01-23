package com.revo.display.views.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.revo.display.R;
import com.revo.display.RevoApplication;
import com.revo.display.bluetooth.ValuesCallback;
import com.revo.display.network.RFirebase;
import com.revo.display.network.ValueCallback;
import com.revo.display.views.custom.Compass;

/**
 * Created by sihrc on 9/20/14.
 */
public class SpectatorFragment extends RevoFragment {
    private RFirebase ref = RevoApplication.app.getFireBaseHelper();
    private Compass compass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.spectator_fragment, container, false);
        compass = (Compass) rootView.findViewById(R.id.compass);

        updateMode();
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        ref.deregisterListener(SpectatorFragment.class.getSimpleName() + "direction", new String[] {"driver", "direction"});
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMode();
    }

    @Override
    public String tag() {
        return SpectatorFragment.class.getSimpleName();
    }

    @Override
    public void setupDriverMode() {
        // do nothing
    }

    @Override
    public void setupNotDriverMode() {
        // Get data from firebase
        ref.registerListener(SpectatorFragment.class.getSimpleName() + "direction", new String[] {"driver", "direction"}, new ValueCallback() {
            @Override
            public void handleValue(Object value) {
                Log.d("spectator handle value", "hello?");
                if (compass != null) {
                    compass.onValueChanged((Long) value);
                }
            }
        });
    }

    @Override
    public ValuesCallback getValuesCallback() {
        return null;
    }
}
