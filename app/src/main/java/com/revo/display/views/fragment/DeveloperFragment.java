package com.revo.display.views.fragment;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.revo.display.R;
import com.revo.display.RevoApplication;
import com.revo.display.bluetooth.ValuesCallback;
import com.revo.display.network.RFirebase;

import java.util.Arrays;

/**
 * Created by sihrc on 9/20/14.
 */
public class DeveloperFragment extends RevoFragment {
    RFirebase ref = RevoApplication.app.getFireBaseHelper();
    Firebase firebaseRef = ref.getRef(new String[]{"developer"});
    Firebase firebaseLogsRef = ref.getRef(new String[]{"logs"});

    TextView console;
    StringBuffer buffer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.developer_fragment, container, false);

        console = (TextView) rootView.findViewById(R.id.can_bus_console);
        buffer = new StringBuffer();

        return rootView;
    }

    @Override
    public String tag() {
        return DeveloperFragment.class.getSimpleName();
    }

    @Override
    public void setupDriverMode() {
        if (receiver != null && activity != null && !registered) {
            registered = true;
            activity.registerReceiver(receiver, new IntentFilter("REVO_APP_DISPLAY"));
        }

        if (ref != null) {
            ref.deregisterListener(DeveloperFragment.class.getSimpleName() + "console", new String[]{"developer", "console"});
            firebaseLogsRef.removeEventListener(consoleData);
        }
    }

    @Override
    public void setupNotDriverMode() {
        // Unregister from driving mode
        if (activity != null && receiver != null && registered) {
            activity.unregisterReceiver(receiver);
            registered = false;
        }

        // Get data from firebase
        firebaseLogsRef.addChildEventListener(consoleData);
//        ref.registerListener(DeveloperFragment.class.getSimpleName() + "console", new String[]{"developer", "console"}, new ValueCallback() {
//            @Override
//            public void handleValue(Object value) {
//                if (value.equals(previous)) {
//                    return;
//                }
//
//                previous = (String) value;
//
//                if (console != null) {
//                    console.setText(buffer.toString());
//                }
//            }
//        });
    }

    ChildEventListener consoleData = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if (buffer == null)
                return;

            buffer.append(dataSnapshot.child("timestamp").getValue());
            buffer.append(" : ");
            buffer.append(dataSnapshot.child("data").getValue());
            buffer.append('\n');

            if (console != null) {
                console.setText(buffer.toString());
            }
        }
        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {}
        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
        @Override
        public void onCancelled(FirebaseError firebaseError) {}
    };

    @Override
    public ValuesCallback getValuesCallback() {
        return new ValuesCallback() {
            @Override
            public void handleValues(byte[] values) {
                String valueStr = Arrays.toString(values);
                buffer.append(valueStr);
                buffer.append('\n');
                firebaseRef.setValue(valueStr);
                if (console != null) {
                    console.setText(buffer.toString());
                }

                Firebase push = firebaseLogsRef.push();
                push.child("timestamp").setValue(System.currentTimeMillis());
                push.child("data").setValue(valueStr);
            }
        };
    }
}
