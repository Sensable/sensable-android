package io.sensable.client.views;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import io.sensable.SensableService;
import io.sensable.client.R;
import io.sensable.client.SensableActivity;
import io.sensable.model.Sensable;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simonmadine on 20/07/2014.
 */
public class RemoteSensablesFragment extends Fragment {

    private static final String TAG = RemoteSensablesFragment.class.getSimpleName();
    public final static String EXTRA_SENSABLE = "io.sensable.sensable";

    private ArrayList<Sensable> mSensables;
    private ArrayAdapter<Sensable> mListArrayAdapter;


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        return layoutInflater.inflate(R.layout.remote_sensables_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialiseList();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
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

    private void initialiseList() {
        final ListView sensableList = (ListView) getView().findViewById(R.id.sensable_list);

        mSensables = new ArrayList<Sensable>();
        mListArrayAdapter = new ArrayAdapter<Sensable>(getActivity(), android.R.layout.simple_list_item_1, mSensables);
        sensableList.setAdapter(mListArrayAdapter);

        //add onclick to listview
        sensableList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), SensableActivity.class);
                intent.putExtra(EXTRA_SENSABLE, (Sensable) parent.getItemAtPosition(position));
                startActivity(intent);
            }
        });
    }

}
