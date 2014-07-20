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
import io.sensable.client.sqlite.SavedSensablesTable;
import io.sensable.client.sqlite.SensableContentProvider;
import io.sensable.model.Sensable;

/**
 * Created by simonmadine on 19/07/2014.
 */
public class FavouriteSensablesFragment extends Fragment {
    private static final String TAG = FavouriteSensablesFragment.class.getSimpleName();
    public final static String EXTRA_SENSABLE = "io.sensable.sensable";

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
        attachDatabaseToList(sensableList);
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

    private void attachDatabaseToList(ListView listView) {
        // Get a cursor with all people
        Cursor c = getActivity().getContentResolver().query(SensableContentProvider.CONTENT_URI,
                SENSABLE_PROJECTION, null, null, null);
        getActivity().startManagingCursor(c);

        ListAdapter adapter = new SimpleCursorAdapter(getActivity(),
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
