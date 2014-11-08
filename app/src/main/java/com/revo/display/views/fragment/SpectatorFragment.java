package com.revo.display.views.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.revo.display.R;
import com.revo.display.RevoApplication;
import com.revo.display.network.FirebaseHelper;
import com.revo.display.network.SnapshotCallback;
import com.revo.display.sensors.OrientationSensor;
import com.revo.display.views.custom.Compass;

/**
 * Created by sihrc on 9/20/14.
 */
public class SpectatorFragment extends RevoFragment {

    private FirebaseHelper firebaseHelper = RevoApplication.app.getFireBaseHelper();
    private Compass compass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.spectator_fragment, container, false);
        compass = (Compass) rootView.findViewById(R.id.compass);

        // read direction data from firebase
        firebaseHelper.registerReader(DIRECTION_KEY, new SnapshotCallback() {
            @Override
            public void handleSnapshot(DataSnapshot snapshot) {
                double direction;
                try {
                    direction = (Double) snapshot.getValue();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    direction = 0;
                }
                compass.onDirectionChange((float) direction);
            }
        });

        return rootView;
    }

    @Override
    public String tag() {
        return SpectatorFragment.class.getSimpleName();
    }
}
