package io.sensable.client;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import io.sensable.SensableService;
import io.sensable.client.adapter.TabsPagerAdapter;
import io.sensable.model.ScheduledSensable;
import io.sensable.model.Sensable;
import io.sensable.model.Statistics;
import io.sensable.model.UserLogin;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by simonmadine on 19/07/2014.
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public final static String EXTRA_SENSABLE = "io.sensable.sensable";

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;

    // Tab titles
    private String[] tabs = {"Favourites", "Local", "Remote"};

    public SensableUser sensableUser;

    //define callback interface
    public interface CallbackInterface {
        void loginStatusUpdate(Boolean loggedIn);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sensableUser = new SensableUser(sharedPref, this);
        if (sensableUser.loggedIn) {
            Toast.makeText(MainActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Not logged In", Toast.LENGTH_SHORT).show();
        }

        initialiseTabs();
    }

    private void initialiseTabs() {
        // Initialization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.saved_sensables, menu);
        menu.findItem(R.id.action_login).setVisible(!sensableUser.loggedIn);
        menu.findItem(R.id.action_logout).setVisible(sensableUser.loggedIn);
        menu.findItem(R.id.action_create).setVisible(sensableUser.loggedIn);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_about) {
            launchAbout();
        } else if (id == R.id.action_login) {
            loginDialog();
        } else if (id == R.id.action_logout) {
            sensableUser.deleteSavedUser(new CallbackInterface() {
                @Override
                public void loginStatusUpdate(Boolean loggedIn) {
                    if (!loggedIn) {
                        Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Logout failed", Toast.LENGTH_SHORT).show();
                    }
                    invalidateOptionsMenu();
                }
            });
        } else if (id == R.id.action_create) {
            createSensable(this.findViewById(android.R.id.content));
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchAbout() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    /**
     * Called when the user clicks the Send button
     */
    public void loginDialog() {
        FragmentManager fm = getFragmentManager();
        SensableLoginFragment sensableLoginFragment = new SensableLoginFragment();
        sensableLoginFragment.setSensableLoginListener(new SensableLoginFragment.SensableLoginListener() {
            @Override
            public void onConfirmed(UserLogin userLogin) {
                sensableUser.login(userLogin, new CallbackInterface() {
                    @Override
                    public void loginStatusUpdate(Boolean loggedIn) {

                        if (loggedIn) {
                            Toast.makeText(MainActivity.this, "Successfully logged In", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                        invalidateOptionsMenu();
                    }
                });
            }
        });
        sensableLoginFragment.show(fm, "sensable_login_name");
    }

    /**
     * Called when the user clicks the Create Sensable menu item
     */
    public void createSensable(View view) {
        FragmentManager fm = getFragmentManager();
        CreateSensableFragment createSensableFragment = new CreateSensableFragment();
        createSensableFragment.setCreateSensableListener(new CreateSensableFragment.CreateSensableListener() {
            @Override
            public void onConfirmed(ScheduledSensable scheduledSensable) {
                Toast.makeText(MainActivity.this, scheduledSensable.getSensorid(), Toast.LENGTH_SHORT).show();
            }
        });
        createSensableFragment.show(fm, "create_sensable_name");
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

}