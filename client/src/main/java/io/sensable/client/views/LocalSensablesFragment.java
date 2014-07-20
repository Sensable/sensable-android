package io.sensable.client.views;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import io.sensable.client.R;
import io.sensable.client.SensableActivity;
import io.sensable.client.scheduler.ScheduleHelper;
import io.sensable.client.sqlite.ScheduledSensableContentProvider;
import io.sensable.client.sqlite.ScheduledSensablesTable;
import io.sensable.model.Sensable;
import io.sensable.model.SensableSender;

/**
 * Created by simonmadine on 19/07/2014.
 */
public class LocalSensablesFragment extends Fragment {
    private static final String TAG = LocalSensablesFragment.class.getSimpleName();

    public final static String EXTRA_SENSABLE = "io.sensable.sensable";

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

        final ListView scheduledSensableList = (ListView) getView().findViewById(R.id.scheduled_sensable_list);
        attachScheduledDatabaseToList(scheduledSensableList);
        final TextView emptyText = (TextView) getView().findViewById(R.id.text_no_local);
        scheduledSensableList.setEmptyView(emptyText);
        //add onclick to ListView

        scheduledSensableList.setOnItemClickListener(getScheduledSensableListener());

    }

    private AdapterView.OnItemClickListener getScheduledSensableListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SensableSender sensableSender = ScheduledSensablesTable.getScheduledSensable((Cursor) parent.getItemAtPosition(position));

                Intent intent = new Intent(getActivity(), SensableActivity.class);
                Sensable sensable = new Sensable();
                sensable.setSensorid(sensableSender.getSensorid());
                sensable.setUnit(sensableSender.getUnit());

                intent.putExtra(EXTRA_SENSABLE, sensable);
                startActivity(intent);

            }
        };
    }

    private void attachScheduledDatabaseToList(ListView listView) {
        // Get a cursor with all people
        Cursor c = getActivity().getContentResolver().query(ScheduledSensableContentProvider.CONTENT_URI,
                SCHEDULED_SENSABLE_PROJECTION, null, null, null);
        getActivity().startManagingCursor(c);

        ListAdapter adapter = new SimpleCursorAdapter(getActivity(),
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


}