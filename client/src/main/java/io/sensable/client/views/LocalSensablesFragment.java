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
import io.sensable.client.sqlite.ScheduledSensableContentProvider;
import io.sensable.client.sqlite.ScheduledSensablesTable;
import io.sensable.client.sqlite.SensableContentProvider;
import io.sensable.model.Sensable;
import io.sensable.model.ScheduledSensable;

/**
 * Created by simonmadine on 19/07/2014.
 */
public class LocalSensablesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = LocalSensablesFragment.class.getSimpleName();
    public final static String EXTRA_SENSABLE = "io.sensable.sensable";

    SensableListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        return layoutInflater.inflate(R.layout.local_sensables_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialiseList();
    }

    private void initialiseList() {
        ScheduleHelper scheduleHelper = new ScheduleHelper(getActivity());
        scheduleHelper.startScheduler();

        final ListView sensableList = (ListView) getView().findViewById(R.id.scheduled_sensable_list);
        attachCursorLoader(sensableList);

        final TextView emptyFavouriteText = (TextView) getView().findViewById(R.id.text_no_local);
        sensableList.setEmptyView(emptyFavouriteText);

        //add onclick to ListView
        sensableList.setOnItemClickListener(getScheduledSensableListener());

    }

    private AdapterView.OnItemClickListener getScheduledSensableListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScheduledSensable scheduledSensable = ScheduledSensablesTable.getScheduledSensable((Cursor) parent.getItemAtPosition(position));

                Intent intent = new Intent(getActivity(), SensableActivity.class);
                Sensable sensable = new Sensable();
                sensable.setSensorid(scheduledSensable.getSensorid());
                sensable.setUnit(scheduledSensable.getUnit());

                intent.putExtra(EXTRA_SENSABLE, sensable);
                startActivity(intent);

            }
        };
    }

    private static final String[] SCHEDULED_SENSABLE_PROJECTION = new String[]{
            ScheduledSensablesTable.COLUMN_ID,
            ScheduledSensablesTable.COLUMN_SENSABLE_ID,
            ScheduledSensablesTable.COLUMN_SENSOR_ID,
            ScheduledSensablesTable.COLUMN_SENSOR_NAME,
            ScheduledSensablesTable.COLUMN_SENSOR_TYPE,
            ScheduledSensablesTable.COLUMN_PENDING,
            ScheduledSensablesTable.COLUMN_UNIT
    };

    private void attachCursorLoader(ListView listView) {
        SensableListAdapter.AdapterHolder projection = new SensableListAdapter.AdapterHolder();
        projection.ID = ScheduledSensablesTable.COLUMN_ID;
        projection.NAME = ScheduledSensablesTable.COLUMN_SENSOR_NAME;
        projection.SENSOR_ID = ScheduledSensablesTable.COLUMN_SENSABLE_ID;
        projection.TYPE = ScheduledSensablesTable.COLUMN_SENSOR_TYPE;
        projection.UNIT = ScheduledSensablesTable.COLUMN_UNIT;


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
        return new CursorLoader(getActivity(), ScheduledSensableContentProvider.CONTENT_URI,
                SCHEDULED_SENSABLE_PROJECTION, null, null, null);
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
