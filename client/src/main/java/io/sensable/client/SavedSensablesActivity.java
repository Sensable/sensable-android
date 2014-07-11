package io.sensable.client;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
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


public class SavedSensablesActivity extends Activity {

    private static final String TAG = SavedSensablesActivity.class.getSimpleName();
    public final static String EXTRA_SENSABLE = "io.sensable.sensable";

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
                Toast.makeText(SavedSensablesActivity.this, sensableSender.getSensable(), Toast.LENGTH_SHORT).show();
            }
        });
        createSensableFragment.show(fm, "create_sensable_name");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.saved_sensables, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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

}