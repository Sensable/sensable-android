package io.sensable.client.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import io.sensable.client.views.FavouriteSensablesFragment;
import io.sensable.client.views.LocalSensablesFragment;
import io.sensable.client.views.RemoteSensablesFragment;

/**
 * Created by simonmadine on 20/07/2014.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new FavouriteSensablesFragment();
            case 1:
                // Games fragment activity
                return new LocalSensablesFragment();
            case 2:
                // Movies fragment activity
                return new RemoteSensablesFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}