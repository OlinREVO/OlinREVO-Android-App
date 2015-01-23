package com.revo.display.views.fragment;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.revo.display.R;
import com.revo.display.RevoApplication;
import com.revo.display.bluetooth.ValuesCallback;
import com.revo.display.network.RFirebase;
import com.revo.display.network.ValueCallback;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by sihrc on 9/20/14.
 */
public class DeveloperFragment extends RevoFragment {
    RFirebase ref = RevoApplication.app.getFireBaseHelper();
    Firebase firebaseRef = ref.getRef(new String[]{"developer"});

    TextView console;
    StringBuffer buffer;
    String previous;

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
        ref.registerListener(DeveloperFragment.class.getSimpleName() + "console", new String[]{"developer", "console"}, new ValueCallback() {
            @Override
            public void handleValue(Object value) {
                if (value.equals(previous)) {
                    return;
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                int seconds = calendar.get(Calendar.SECOND);

                previous = (String) value;
                buffer.append('[');
                buffer.append(hours < 10 ? "0" + hours : hours);
                buffer.append(':');
                buffer.append(minutes < 10 ? "0" + minutes : minutes);
                buffer.append(':');
                buffer.append(seconds < 10 ? "0" + seconds : seconds);
                buffer.append("] ");
                buffer.append(previous);
                buffer.append('\n');
                if (console != null) {
                    console.setText(buffer.toString());
                }
            }
        });
    }

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
            }
        };
    }
}
