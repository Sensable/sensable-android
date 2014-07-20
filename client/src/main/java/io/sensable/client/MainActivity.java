package io.sensable.client;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import io.sensable.model.SensableSender;
import io.sensable.model.UserLogin;

/**
 * Created by simonmadine on 19/07/2014.
 */
public class MainActivity extends Activity {

    private static final String TAG = SavedSensablesActivity.class.getSimpleName();
    public final static String EXTRA_SENSABLE = "io.sensable.sensable";

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
        if (id == R.id.action_login) {
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
            listSensors(this.findViewById(android.R.id.content));
        }
        return super.onOptionsItemSelected(item);
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
    public void listSensors(View view) {
        FragmentManager fm = getFragmentManager();
        CreateSensableFragment createSensableFragment = new CreateSensableFragment();
        createSensableFragment.setCreateSensableListener(new CreateSensableFragment.CreateSensableListener() {
            @Override
            public void onConfirmed(SensableSender sensableSender) {
                Toast.makeText(MainActivity.this, sensableSender.getSensorid(), Toast.LENGTH_SHORT).show();
            }
        });
        createSensableFragment.show(fm, "create_sensable_name");
    }

}