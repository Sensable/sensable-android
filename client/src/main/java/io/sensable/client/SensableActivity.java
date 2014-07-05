package io.sensable.client;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import io.sensable.SensableService;
import io.sensable.client.sqlite.SavedSensablesTable;
import io.sensable.client.sqlite.SensableContentProvider;
import io.sensable.model.Sample;
import io.sensable.model.Sensable;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.Arrays;


public class SensableActivity extends Activity {

    private static final String TAG = SensableActivity.class.getSimpleName();

    private Sensable sensable;
    private TextView sensableId;
    private TextView sensableUnit;
    private TextView sensableLocation;
    private ListView sensableSamples;

    private Boolean savedLocally;

    private Button favouriteButton;

    private Button unFavouriteButton;

    private ArrayList<Sample> mSamples;
    private ArrayAdapter<Sample> mListArrayAdapter;

    private SensableContentProvider sensableContentProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensable);
        Intent intent = getIntent();
        sensable = (Sensable) intent.getParcelableExtra(SensablesListActivity.EXTRA_SENSABLE);

        sensableId = (TextView) findViewById(R.id.sensable_id_field);
        sensableUnit = (TextView) findViewById(R.id.sensable_unit_field);
        sensableLocation = (TextView) findViewById(R.id.sensable_location_field);
        sensableSamples = (ListView) findViewById(R.id.sensable_samples);


        mSamples = new ArrayList<Sample>(Arrays.asList(sensable.getSamples()));
        mListArrayAdapter = new ArrayAdapter<Sample>(SensableActivity.this, android.R.layout.simple_list_item_1, mSamples);
        sensableSamples.setAdapter(mListArrayAdapter);

        savedLocally = checkSavedLocally();

        favouriteButton = (Button) findViewById(R.id.favourite_sensables_button);
        unFavouriteButton = (Button) findViewById(R.id.unfavourite_sensables_button);

        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveThisSensable();
            }
        });

        unFavouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsaveThisSensable();
            }
        });

        updateView(sensable);
    }

    private void saveThisSensable() {
        // Defines a new Uri object that receives the result of the insertion
        Uri mNewUri;

        savedLocally = checkSavedLocally();

        if (!savedLocally) {
            // Defines an object to contain the new values to insert
            ContentValues mNewValues = new ContentValues();

        /*
         * Sets the values of each column and inserts the word. The arguments to the "put"
         * method are "column name" and "value"
         */
            mNewValues.put(SavedSensablesTable.COLUMN_LOCATION_LATITUDE, sensable.getLocation()[0]);
            mNewValues.put(SavedSensablesTable.COLUMN_LOCATION_LONGITUDE, sensable.getLocation()[1]);
            mNewValues.put(SavedSensablesTable.COLUMN_SENSOR_ID, sensable.getSensorid());
            mNewValues.put(SavedSensablesTable.COLUMN_UNIT, sensable.getUnit());

            mNewUri = getContentResolver().insert(
                    SensableContentProvider.CONTENT_URI,   // the user dictionary content URI
                    mNewValues                          // the values to insert
            );
            savedLocally = true;
            updateSaveButton();
        }

    }

    private void unsaveThisSensable() {
        // Defines a new Uri object that receives the result of the insertion
        int rowsDeleted;

        savedLocally = checkSavedLocally();
        if (savedLocally) {
            // Defines an object to contain the new values to insert
            ContentValues mNewValues = new ContentValues();

        /*
         * Sets the values of each column and inserts the word. The arguments to the "put"
         * method are "column name" and "value"
         */
            mNewValues.put(SavedSensablesTable.COLUMN_LOCATION_LATITUDE, sensable.getLocation()[0]);
            mNewValues.put(SavedSensablesTable.COLUMN_LOCATION_LONGITUDE, sensable.getLocation()[1]);
            mNewValues.put(SavedSensablesTable.COLUMN_SENSOR_ID, sensable.getSensorid());
            mNewValues.put(SavedSensablesTable.COLUMN_UNIT, sensable.getUnit());

            rowsDeleted = getContentResolver().delete(getDatabaseUri(), null, null);
            savedLocally = !(rowsDeleted > 0);
            updateSaveButton();
        }
    }

    private boolean checkSavedLocally() {
        Cursor count = getContentResolver().query(getDatabaseUri(), new String[]{"*"}, null, null, null, null);
        return count.getCount() > 0;
    }

    // Returns the DB URI for this sensable
    private Uri getDatabaseUri() {
        return Uri.parse(SensableContentProvider.CONTENT_URI.toString()+"/"+sensable.getSensorid());
    }

    @Override
    public void onStart() {
        super.onStart();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://sensable.io")
                .build();

        SensableService service = restAdapter.create(SensableService.class);

        service.getSensorData(sensable.getSensorid(), new Callback<Sensable>() {
            @Override
            public void success(Sensable sensable, Response response) {
                Log.d(TAG, "Callback Success - Sensable");
                updateView(sensable);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Callback failure" + retrofitError.toString());
            }
        });
    }

    public void updateView(Sensable sensable) {
        sensableId.setText(sensable.getSensorid());

        sensableUnit.setText(sensable.getUnit());

        sensableLocation.setText(sensable.getLocation()[0] + ", " + sensable.getLocation()[1]);

        mSamples.clear();
        mSamples.addAll(new ArrayList<Sample>(Arrays.asList(sensable.getSamples())));
        mListArrayAdapter.notifyDataSetChanged();
        updateSaveButton();
    }

    public void updateSaveButton() {
        if(savedLocally) {
            favouriteButton.setVisibility(View.GONE);
            unFavouriteButton.setVisibility(View.VISIBLE);
        } else {
            favouriteButton.setVisibility(View.VISIBLE);
            unFavouriteButton.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sensable, menu);
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
}
