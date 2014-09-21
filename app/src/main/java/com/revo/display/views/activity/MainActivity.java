package com.revo.display.views.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.revo.display.R;
import com.revo.display.views.fragment.DriverFragment;
import com.revo.display.views.fragment.RevoFragment;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 */
public class MainActivity extends Activity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        fragmentManager = getFragmentManager();
        setupDrawer();
    }

    private String[] sectionTitles = new String[] {"Driver", "Spectator", "Developer"};
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private void setupDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, sectionTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        showFragment(new DriverFragment());
    }

    private class DrawerItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = new DriverFragment();

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void showFragment(RevoFragment fragment) {
        //Check to see if previously rendered fragment is still alive
        Log.i("MainActivity", "Showing fragment: " + fragment.tag());
        Fragment old = fragmentManager.findFragmentByTag(fragment.tag());
        if (old == null) {
            Log.i("DebugDebug", "Here");
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            return;
        }
        getFragmentManager().beginTransaction().replace(R.id.content_frame, old).commit();
        old.onResume();
    }
}
