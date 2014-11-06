package com.revo.display.views.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.revo.display.R;
import com.revo.display.RevoApplication;
import com.revo.display.network.RFirebase;
import com.revo.display.views.custom.RSpeedometer;
import com.revo.display.views.custom.RBatteryMeter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sihrc on 9/20/14.
 */
public class DriverFragment extends RevoFragment {
    RFirebase RFirebase = RevoApplication.app.getFireBaseHelper();

    //Parameters for fake throttle
    int currentSpeed;
    int currentCharge;
    boolean accelerating;

    //Views for Display
    RSpeedometer RSpeedometer;
    RBatteryMeter RBatteryMeter;
    ImageView battery;

    //For updating the speedometer
    Timer timer;
    boolean timerRunning;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.driver_fragment, container, false);
        RSpeedometer = (RSpeedometer) rootView.findViewById(R.id.speedometer);
        RBatteryMeter = (RBatteryMeter) rootView.findViewById((R.id.batterymeter));

//        battery = (ImageView) rootView.findViewById(R.id.image);
//        battery.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//               battery.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                RBatteryMeter.setRectDimensions(
//                        (int) battery.getY(),
//                        (int) battery.getY() + battery.getHeight(),
//                        (int) battery.getX() + battery.getWidth(),
//                        (int) battery.getX());
//            }
//        });

        rootView.findViewById(R.id.throttle).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    accelerating = true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    accelerating = false;
                }
                return false;
            }
        });



        return rootView;
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
                            if (!accelerating) {
                                currentSpeed = currentSpeed > 0 ? currentSpeed - 2 : 0;
                                currentCharge = currentCharge > 0 ? currentCharge - 2 : 0;
                                Log.i("fake_throttle", "accelerating " + currentSpeed);
                            } else {
                                currentSpeed = currentSpeed < 100 ? (currentSpeed + (100 - currentSpeed)/10) : 100;
                                currentCharge = currentCharge < 100 ? (currentCharge + (100 - currentCharge)/10) : 100;
                                Log.i("fake_throttle", "accelerating " + currentSpeed);
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            RSpeedometer.onSpeedChanged(currentSpeed);
                            RBatteryMeter.onChargeChanged(currentCharge);
                            RFirebase.write("Charge", "" + currentCharge);
                            RFirebase.write("Speed", "" + currentSpeed);
                        }
                    }.execute();
                }
            }, 0, 250);
            timerRunning = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timerRunning && timer != null) {
            timerRunning = false;
            timer.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startRunning();
    }

    @Override
    public String tag() {
        return DriverFragment.class.getSimpleName();
    }
}
