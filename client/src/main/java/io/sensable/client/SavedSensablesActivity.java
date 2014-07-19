package io.sensable.client;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import io.sensable.client.scheduler.ScheduleHelper;
import io.sensable.client.sqlite.SavedSensablesTable;
import io.sensable.client.sqlite.ScheduledSensableContentProvider;
import io.sensable.client.sqlite.ScheduledSensablesTable;
import io.sensable.client.sqlite.SensableContentProvider;
import io.sensable.model.Sensable;
import io.sensable.model.SensableSender;
import io.sensable.model.UserLogin;


public class SavedSensablesActivity extends Activity {

    private static final String TAG = SavedSensablesActivity.class.getSimpleName();
    public final static String EXTRA_SENSABLE = "io.sensable.sensable";

    public SensableUser sensableUser;
    private Button createSensableButton;


    //define callback interface
    public interface CallbackInterface {

        void loginStatusUpdate(Boolean loggedIn);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_sensables);

        ScheduleHelper scheduleHelper = new ScheduleHelper(this);
        scheduleHelper.startScheduler();

        final ListView scheduledSensableList = (ListView) findViewById(R.id.scheduled_sensable_list);
        attachScheduledDatabaseToList(scheduledSensableList);
        final TextView emptyText = (TextView) findViewById(R.id.text_no_local);
        scheduledSensableList.setEmptyView(emptyText);
        //add onclick to ListView
        scheduledSensableList.setOnItemClickListener(getScheduledSensableListener());


        final ListView sensableList = (ListView) findViewById(R.id.saved_sensable_list);
        attachDatabaseToList(sensableList);
        final TextView emptyFavouriteText = (TextView) findViewById(R.id.text_no_favourite);
        sensableList.setEmptyView(emptyFavouriteText);

        //add onclick to ListView
        sensableList.setOnItemClickListener(getSavedSensableListener());

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sensableUser = new SensableUser(sharedPref, this);
        createSensableButton = (Button) findViewById(R.id.show_create_sensable_dialog);
        if (sensableUser.loggedIn) {
            createSensableButton.setVisibility(View.VISIBLE);
            Toast.makeText(SavedSensablesActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
        } else {
            createSensableButton.setVisibility(View.GONE);
            Toast.makeText(SavedSensablesActivity.this, "Not logged In", Toast.LENGTH_SHORT).show();
        }

    }

    private AdapterView.OnItemClickListener getSavedSensableListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SavedSensablesActivity.this, SensableActivity.class);
                Sensable sensable = SavedSensablesTable.getSensable((Cursor) parent.getItemAtPosition(position));
                intent.putExtra(EXTRA_SENSABLE, sensable);
                startActivity(intent);
            }
        };
    }

    private AdapterView.OnItemClickListener getScheduledSensableListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SensableSender sensableSender = ScheduledSensablesTable.getScheduledSensable((Cursor) parent.getItemAtPosition(position));

                Intent intent = new Intent(SavedSensablesActivity.this, SensableActivity.class);
                Sensable sensable = new Sensable();
                sensable.setSensorid(sensableSender.getSensorid());
                sensable.setUnit(sensableSender.getUnit());

                intent.putExtra(EXTRA_SENSABLE, sensable);
                startActivity(intent);

            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * Called when the user clicks the list all button
     */
    public void showAllSensables(View view) {
        Intent intent = new Intent(SavedSensablesActivity.this, SensablesListActivity.class);
        startActivity(intent);
    }

    /**
     * Called when the user clicks the Send button
     */
    public void listSensors(View view) {
        FragmentManager fm = getFragmentManager();
        CreateSensableFragment createSensableFragment = new CreateSensableFragment();
        createSensableFragment.setCreateSensableListener(new CreateSensableFragment.CreateSensableListener() {
            @Override
            public void onConfirmed(SensableSender sensableSender) {
                Toast.makeText(SavedSensablesActivity.this, sensableSender.getSensorid(), Toast.LENGTH_SHORT).show();
            }
        });
        createSensableFragment.show(fm, "create_sensable_name");
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
                            createSensableButton.setVisibility(View.VISIBLE);
                            Toast.makeText(SavedSensablesActivity.this, "Successfully logged In", Toast.LENGTH_SHORT).show();
                        } else {
                            createSensableButton.setVisibility(View.GONE);
                            Toast.makeText(SavedSensablesActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                        invalidateOptionsMenu();
                    }
                });
            }
        });
        sensableLoginFragment.show(fm, "sensable_login_name");
    }

    private void attachDatabaseToList(ListView listView) {
        // Get a cursor with all people
        Cursor c = getContentResolver().query(SensableContentProvider.CONTENT_URI,
                SENSABLE_PROJECTION, null, null, null);
        startManagingCursor(c);

        ListAdapter adapter = new SimpleCursorAdapter(this,
                // Use a template that displays a text view
                android.R.layout.simple_list_item_1,
                // Give the cursor to the list adapter
                c,
                // Map the NAME column in the people database to...
                new String[]{SavedSensablesTable.COLUMN_SENSOR_ID},
                // The "text1" view defined in the XML template
                new int[]{android.R.id.text1});
        listView.setAdapter(adapter);
    }

    private static final String[] SENSABLE_PROJECTION = new String[]{
            SavedSensablesTable.COLUMN_ID,
            SavedSensablesTable.COLUMN_SENSOR_ID,
            SavedSensablesTable.COLUMN_LOCATION_LATITUDE,
            SavedSensablesTable.COLUMN_LOCATION_LONGITUDE,
            SavedSensablesTable.COLUMN_UNIT
    };

    private void attachScheduledDatabaseToList(ListView listView) {
        // Get a cursor with all people
        Cursor c = getContentResolver().query(ScheduledSensableContentProvider.CONTENT_URI,
                SCHEDULED_SENSABLE_PROJECTION, null, null, null);
        startManagingCursor(c);

        ListAdapter adapter = new SimpleCursorAdapter(this,
                // Use a template that displays a text view
                android.R.layout.simple_list_item_1,
                // Give the cursor to the list adapter
                c,
                // Map the NAME column in the people database to...
                new String[]{ScheduledSensablesTable.COLUMN_SENSABLE_ID},
                // The "text1" view defined in the XML template
                new int[]{android.R.id.text1});
        listView.setAdapter(adapter);
    }

    private static final String[] SCHEDULED_SENSABLE_PROJECTION = new String[]{
            ScheduledSensablesTable.COLUMN_ID,
            ScheduledSensablesTable.COLUMN_SENSABLE_ID,
            ScheduledSensablesTable.COLUMN_SENSOR_ID,
            ScheduledSensablesTable.COLUMN_SENSOR_TYPE,
            ScheduledSensablesTable.COLUMN_PENDING,
            ScheduledSensablesTable.COLUMN_UNIT
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.saved_sensables, menu);
        if (sensableUser.loggedIn) {
            menu.findItem(R.id.action_login).setVisible(false);
            menu.findItem(R.id.action_logout).setVisible(true);
        } else {
            menu.findItem(R.id.action_login).setVisible(true);
            menu.findItem(R.id.action_logout).setVisible(false);
        }
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
                        Toast.makeText(SavedSensablesActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                        createSensableButton.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(SavedSensablesActivity.this, "Logout failed", Toast.LENGTH_SHORT).show();
                        createSensableButton.setVisibility(View.VISIBLE);
                    }
                    invalidateOptionsMenu();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}