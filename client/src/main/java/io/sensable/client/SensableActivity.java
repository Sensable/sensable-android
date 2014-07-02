package io.sensable.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import io.sensable.SensableService;
import io.sensable.model.Sample;
import io.sensable.model.Sensable;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SensableActivity extends Activity {

    private static final String TAG = SensableActivity.class.getSimpleName();

    private Sensable sensable;
    private TextView sensableId;
    private TextView sensableUnit;
    private TextView sensableLocation;
    private ListView sensableSamples;

    private ArrayList<Sample> mSamples;
    private ArrayAdapter<Sample> mListArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensable);
        Intent intent = getIntent();
        sensable = intent.getParcelableExtra(SensablesListActivity.EXTRA_SENSABLE);

        sensableId = (TextView) findViewById(R.id.sensable_id_field);
        sensableUnit = (TextView) findViewById(R.id.sensable_unit_field);
        sensableLocation = (TextView) findViewById(R.id.sensable_location_field);
        sensableSamples = (ListView) findViewById(R.id.sensable_samples);


        mSamples = new ArrayList<Sample>( Arrays.asList( sensable.getSamples()) );
        mListArrayAdapter = new ArrayAdapter<Sample>(SensableActivity.this, android.R.layout.simple_list_item_1, mSamples);
        sensableSamples.setAdapter(mListArrayAdapter);

        updateView(sensable);
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
        mSamples.addAll(new ArrayList<Sample>( Arrays.asList( sensable.getSamples()) ));
        mListArrayAdapter.notifyDataSetChanged();
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
