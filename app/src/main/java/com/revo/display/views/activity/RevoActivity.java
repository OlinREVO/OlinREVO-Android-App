package com.revo.display.views.activity;

import android.app.Activity;
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
import com.revo.display.RevoApplication;
import com.revo.display.views.fragment.DeveloperFragment;
import com.revo.display.views.fragment.DriverFragment;
import com.revo.display.views.fragment.RevoFragment;
import com.revo.display.views.fragment.SpectatorFragment;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class RevoActivity extends Activity {

    // Fragment Management
    FragmentManager fragmentManager;
    RevoFragment currentFragment;

    //Drawer Management
    private String[] sectionTitles = new String[]{"Driver", "Spectator", "Developer"};
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting up the Views for Activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.revo_activity);

        //Initializing Navigation Management
        fragmentManager = getFragmentManager();
        setupDrawer();
    }

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

    public void showFragment(RevoFragment fragment) {
        Log.i("MainActivity", "Showing fragment: " + fragment.tag());
        RevoFragment old = (RevoFragment) fragmentManager.findFragmentByTag(fragment.tag());
        if (old == null) {
            Log.i("MainActivity", "Using a new fragment for " + fragment.tag());
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            currentFragment = fragment;
            return;
        }

        Log.i("MainActivity", "Found an old fragment for " + old.tag());
        getFragmentManager().beginTransaction().replace(R.id.content_frame, old).commit();
        old.onResume();
        currentFragment = old;
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        RevoFragment fragment;
        switch (position) {
            case 0:
                fragment = new DriverFragment();
                break;
            case 1:
                fragment = new SpectatorFragment();
                break;
            case 2:
                fragment = new DeveloperFragment();
                break;
            case 3:
                boolean notChecked = !mDrawerList.isItemChecked(position);
                RevoApplication.isDriver = notChecked;
                mDrawerList.setItemChecked(position, notChecked);
                currentFragment.updateMode();
                return;
            default:
                return;
        }

        // Insert the fragment by replacing any existing fragment
        showFragment(fragment);

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }



    private class DrawerItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}
