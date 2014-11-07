package com.revo.display.views.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.revo.display.R;
import com.revo.display.RevoApplication;
import com.revo.display.network.RFirebase;
import com.revo.display.network.SnapshotCallback;
import com.revo.display.views.custom.RBatteryMeter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sihrc on 9/20/14.
 */
public class SpectatorFragment extends RevoFragment {
    Timer timer;
    boolean timerRunning;
    RFirebase RFirebase = RevoApplication.app.getFireBaseHelper();
    RBatteryMeter RBatteryMeter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.spectator_fragment, container);
        RBatteryMeter = (RBatteryMeter) rootView.findViewById((R.id.batterymeter));
        RFirebase.registerReader("Charge", new SnapshotCallback() {
            @Override
            public void handleSnapshot(DataSnapshot snapshot) {
                RBatteryMeter.onChargeChanged(Float.parseFloat((String)snapshot.getValue()));
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void startRunning() {
        if (!timerRunning) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    new AsyncTask<Void, Integer, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {

                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {

                        }
                    }.execute();
                }
            }, 0, 250);
            timerRunning = true;
        }
    }

    @Override
    public String tag() {
        return SpectatorFragment.class.getSimpleName();
    }

    @Override
    public void onResume() {
        super.onResume();
        startRunning();
    }
}
