package io.sensable.client;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import io.sensable.client.sqlite.SavedSensablesTable;
import io.sensable.client.sqlite.SensableContentProvider;
import io.sensable.model.Sensable;
import io.sensable.model.SensableSender;
import io.sensable.model.UserLogin;


public class SavedSensablesActivity extends Activity {

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
        setContentView(R.layout.activity_saved_sensables);

        final ListView sensableList = (ListView) findViewById(R.id.saved_sensable_list);
        attachDatabaseToList(sensableList);
        final TextView emptyText = (TextView) findViewById(R.id.text_no_favourite);
        sensableList.setEmptyView(emptyText);

        //add onclick to ListView
        sensableList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SavedSensablesActivity.this, SensableActivity.class);
                Sensable sensable = SavedSensablesTable.getSensable((Cursor) parent.getItemAtPosition(position));
                intent.putExtra(EXTRA_SENSABLE, sensable);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sensableUser = new SensableUser(sharedPref, this);
        if(sensableUser.loggedIn) {
            Toast.makeText(SavedSensablesActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(SavedSensablesActivity.this, "Not logged In", Toast.LENGTH_SHORT).show();
        }

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
                Toast.makeText(SavedSensablesActivity.this, sensableSender.getSensable().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        createSensableFragment.show(fm, "create_sensable_name");
    }

    /**
     * Called when the user clicks the Send button
     */
    public void loginDialog() {
        FragmentManager fm = getFragmentManager();
        SensableLoginFragment sensableLoginFragment = new SensableLoginFragment ();
        sensableLoginFragment.setSensableLoginListener(new SensableLoginFragment.SensableLoginListener() {
            @Override
            public void onConfirmed(UserLogin userLogin) {
                sensableUser.login(userLogin, new CallbackInterface() {
                    @Override
                    public void loginStatusUpdate(Boolean loggedIn) {
                        if(loggedIn) {
                            Toast.makeText(SavedSensablesActivity.this, "Successfully logged In", Toast.LENGTH_SHORT).show();
                        } else {
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.saved_sensables, menu);
        if(sensableUser.loggedIn) {
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
                    if(!loggedIn) {
                        Toast.makeText(SavedSensablesActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SavedSensablesActivity.this, "Logout failed", Toast.LENGTH_SHORT).show();
                    }
                    invalidateOptionsMenu();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

}