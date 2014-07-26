package io.sensable.client.views;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import io.sensable.client.R;
import io.sensable.client.SensableActivity;
import io.sensable.client.scheduler.ScheduleHelper;
import io.sensable.client.sqlite.SavedSensablesTable;
import io.sensable.client.sqlite.SensableContentProvider;
import io.sensable.model.Sensable;

/**
 * Created by simonmadine on 19/07/2014.
 */
public class FavouriteSensablesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = FavouriteSensablesFragment.class.getSimpleName();
    public final static String EXTRA_SENSABLE = "io.sensable.sensable";

    SensableListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        return layoutInflater.inflate(R.layout.favourite_sensables_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialiseList();
    }

    private void initialiseList() {
        ScheduleHelper scheduleHelper = new ScheduleHelper(getActivity());
        scheduleHelper.startScheduler();

        final ListView sensableList = (ListView) getView().findViewById(R.id.saved_sensable_list);
        attachCursorLoader(sensableList);

        final TextView emptyFavouriteText = (TextView) getView().findViewById(R.id.text_no_favourite);
        sensableList.setEmptyView(emptyFavouriteText);

        //add onclick to ListView
        sensableList.setOnItemClickListener(getSavedSensableListener());

    }

    private AdapterView.OnItemClickListener getSavedSensableListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), SensableActivity.class);
                Sensable sensable = SavedSensablesTable.getSensable((Cursor) parent.getItemAtPosition(position));
                intent.putExtra(EXTRA_SENSABLE, sensable);
                startActivity(intent);
            }
        };
    }

    private static final String[] SENSABLE_PROJECTION = new String[]{
            SavedSensablesTable.COLUMN_ID,
            SavedSensablesTable.COLUMN_SENSOR_ID,
            SavedSensablesTable.COLUMN_SENSOR_TYPE,
            SavedSensablesTable.COLUMN_NAME,
            SavedSensablesTable.COLUMN_LOCATION_LATITUDE,
            SavedSensablesTable.COLUMN_LOCATION_LONGITUDE,
            SavedSensablesTable.COLUMN_LAST_SAMPLE,
            SavedSensablesTable.COLUMN_UNIT
    };

    private void attachCursorLoader(ListView listView) {
        SensableListAdapter.AdapterHolder projection = new SensableListAdapter.AdapterHolder();
        projection.ID = SavedSensablesTable.COLUMN_ID;
        projection.NAME = SavedSensablesTable.COLUMN_NAME;
        projection.SENSOR_ID = SavedSensablesTable.COLUMN_SENSOR_ID;
        projection.VALUE = SavedSensablesTable.COLUMN_LAST_SAMPLE;
        projection.TYPE = SavedSensablesTable.COLUMN_SENSOR_TYPE;
        projection.UNIT = SavedSensablesTable.COLUMN_UNIT;

        mAdapter = new SensableListAdapter(getActivity(), R.id.row_sensable_id, null, projection);
        listView.setAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(getActivity(), SensableContentProvider.CONTENT_URI,
                SENSABLE_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)

        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }
}
