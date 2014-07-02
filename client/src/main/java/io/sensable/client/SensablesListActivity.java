package io.sensable.client;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import io.sensable.SensableService;
import io.sensable.model.Sensable;
import io.sensable.model.SensableSender;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madine on 01/07/14.
 */
public class SensablesListActivity extends FragmentActivity {

    private static final String TAG = SensablesListActivity.class.getSimpleName();
    public final static String EXTRA_SENSABLE = "io.sensable.sensable";

    private ArrayList<Sensable> mSensables;
    private ArrayAdapter<Sensable> mListArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensable_list_layout);

        final ListView sensableList = (ListView) findViewById(R.id.sensable_list);

        mSensables = new ArrayList<Sensable>();
        mListArrayAdapter = new ArrayAdapter<Sensable>(SensablesListActivity.this, android.R.layout.simple_list_item_1, mSensables);
        sensableList.setAdapter(mListArrayAdapter);

        //add onclick to listview
        sensableList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SensablesListActivity.this, SensableActivity.class);
                intent.putExtra(EXTRA_SENSABLE, (Sensable) parent.getItemAtPosition(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://sensable.io")
                .build();

        SensableService service = restAdapter.create(SensableService.class);

        service.listSensables(new Callback<List<Sensable>>() {
            @Override
            public void success(List<Sensable> sensables, Response response) {
                Log.d(TAG, "Callback Success" + sensables.size());
                mSensables.clear();
                mSensables.addAll(sensables);
                mListArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Callback failure" + retrofitError.toString());
            }
        });
    }

    /**
     * Called when the user clicks the Send button
     */
    public void sendMessage(View view) {
        FragmentManager fm = getFragmentManager();
        CreateSensableFragment createSensableFragment = new CreateSensableFragment();
        createSensableFragment.setCreateSensableListener(new CreateSensableFragment.CreateSensableListener() {
            @Override
            public void onConfirmed(SensableSender sensableSender) {
                Toast.makeText(SensablesListActivity.this, sensableSender.getSensable(), Toast.LENGTH_SHORT).show();
            }
        });
        createSensableFragment.show(fm, "create_sensable_name");
    }

    //onlistviewclick
    //create intent
    //add sensable as extra

}
