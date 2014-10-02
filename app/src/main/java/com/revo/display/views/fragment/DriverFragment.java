package com.revo.display.views.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.revo.display.R;
import com.revo.display.RevoApplication;
import com.revo.display.network.FirebaseHelper;
import com.revo.display.views.custom.Speedometer;

/**
 * Created by sihrc on 9/20/14.
 */
public class DriverFragment extends RevoFragment {
    FirebaseHelper firebaseHelper = RevoApplication.app.getFireBaseHelper();

    //Parameters for fake throttle
    int currentSpeed;
    boolean accelerating;
    Thread accelerator;
    Speedometer speedometer;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.driver_fragment, container, false);

        speedometer = (Speedometer) rootView.findViewById(R.id.speedometer);

        accelerator = new Thread() {
            @Override
            public void run() {
                while (true) {
                    Log.i("DebugDebug", "Looping");
                    if (!accelerating) {
                        currentSpeed = currentSpeed > 0 ? currentSpeed - 2 : 0;
                        speedometer.setCurrentSpeed(currentSpeed);
                        Log.i("fake_throttle", "accelerating " + currentSpeed);
                    } else {
//                        currentSpeed = currentSpeed < 100 ? (int) (currentSpeed + (100 - speedometer.getCurrentSpeed())/100) : 100;
                        currentSpeed = currentSpeed < 100 ? currentSpeed + 1 : 100;
                        speedometer.setCurrentSpeed(currentSpeed);
                        Log.i("fake_throttle", "accelerating " + currentSpeed);
                    }
                }
            }
        };

        new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                speedometer.setCurrentSpeed((Integer) message.obj);
                Log.i("DebugDebug", "Setting");
//                speedometer.invalidate();
                return false;
            }
        }).post(accelerator);

        rootView.findViewById(R.id.throttle).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i("DebugDebug", "Hello");
                    accelerating = true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    accelerating = false;
                }
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        accelerator.interrupt();
    }

    @Override
    public void onResume() {
        super.onResume();
        accelerator.start();
    }

    @Override
    public String tag() {
        return DriverFragment.class.getSimpleName();
    }
}
