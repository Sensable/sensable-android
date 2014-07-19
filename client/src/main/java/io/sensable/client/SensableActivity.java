package io.sensable.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import io.sensable.SensableService;
import io.sensable.client.scheduler.ScheduleHelper;
import io.sensable.client.sqlite.SavedSensablesTable;
import io.sensable.client.sqlite.ScheduledSensableContentProvider;
import io.sensable.client.sqlite.ScheduledSensablesTable;
import io.sensable.client.sqlite.SensableContentProvider;
import io.sensable.model.Sample;
import io.sensable.model.Sensable;
import io.sensable.model.SensableSender;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.*;


public class SensableActivity extends Activity {

    private static final String TAG = SensableActivity.class.getSimpleName();

    private Sensable sensable;
    private TextView sensableId;
    private TextView sensableUnit;
    private TextView sensableLocation;

    ExpandableListAdapter mExpandableListAdapter;
    ExpandableListView sensableSamples;
    List<String> listDataHeader = new ArrayList<String>();
    HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();

    private Boolean savedLocally;

    private Button favouriteButton;

    private Button unFavouriteButton;

    private ArrayList<Sample> mSamples;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensable);
        Intent intent = getIntent();
        sensable = (Sensable) intent.getParcelableExtra(SensablesListActivity.EXTRA_SENSABLE);

        sensableId = (TextView) findViewById(R.id.sensable_id_field);
        sensableUnit = (TextView) findViewById(R.id.sensable_unit_field);
        sensableLocation = (TextView) findViewById(R.id.sensable_location_field);

        if (sensable.getLocation() == null) {
            sensable.setLocation(new double[]{0, 0});
        }

        if (sensable.getSamples() == null) {
            sensable.setSamples(new Sample[]{});
        }
        mSamples = new ArrayList<Sample>(Arrays.asList(sensable.getSamples()));

        // get the listview
        sensableSamples = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        mExpandableListAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        sensableSamples.setAdapter(mExpandableListAdapter);

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

        /* If this is a local sender, make the delete button visible */
        final Cursor localSender = getSensableSender();
        if (localSender.getCount() > 0) {
            final Button deleteLocal = (Button) findViewById(R.id.delete_local_button);
            deleteLocal.setVisibility(View.VISIBLE);
            deleteLocal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SensableActivity.this);
                    builder.setMessage("Stop sampling with this sensable?");
                    builder.setTitle("Confirmation Dialog");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ScheduleHelper scheduleHelper = new ScheduleHelper(SensableActivity.this);
                            SensableSender sensableSender = ScheduledSensablesTable.getScheduledSensable(localSender);
                            scheduleHelper.removeSensableFromScheduler(sensableSender);
                            deleteLocal.setVisibility(View.GONE);
                            Toast.makeText(SensableActivity.this, "Sensable stopped", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.create().show();


                }
            });
        }

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
        mSamples.addAll(new ArrayList<Sample>(Arrays.asList(sensable.getSamples())));
        prepareListData();
        mExpandableListAdapter.notifyDataSetChanged();
        updateSaveButton();
    }


    private void saveThisSensable() {
        savedLocally = checkSavedLocally();

        if (!savedLocally) {
            Uri mNewUri;
            ContentValues mNewValues = SavedSensablesTable.serializeSensableForSqlLite(sensable);

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
            rowsDeleted = getContentResolver().delete(getDatabaseUri(), null, null);
            savedLocally = !(rowsDeleted > 0);
            updateSaveButton();
        }
    }

    private boolean checkSavedLocally() {
        Cursor count = getContentResolver().query(getDatabaseUri(), new String[]{"*"}, null, null, null, null);
        return count.getCount() > 0;
    }

    private Cursor getSensableSender() {
        Cursor count = getContentResolver().query(getScheduledDatabaseUri(), new String[]{"*"}, null, null, null, null);
        return count;
    }

    // Returns the DB URI for this sensable
    private Uri getDatabaseUri() {
        return Uri.parse(SensableContentProvider.CONTENT_URI + "/" + sensable.getSensorid());
    }

    private Uri getScheduledDatabaseUri() {
        return Uri.parse(ScheduledSensableContentProvider.CONTENT_URI + "/" + sensable.getSensorid());
    }


    public void updateSaveButton() {
        if (savedLocally) {
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

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader.clear();//
        listDataChild.clear();//

        // Reverse the order of samples so they are by timestamp desc
        Collections.sort(mSamples, new Comparator<Sample>() {
            @Override
            public int compare(Sample a, Sample b) {
                return (int) (b.getTimestamp() - a.getTimestamp());
            }
        });

        List<ArrayList<String>> currentListList = new ArrayList<ArrayList<String>>();
        currentListList.add(new ArrayList<String>());

        String thisDayName;
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < mSamples.size() - 1; i++) {
            int currentIndex = currentListList.size() - 1;
            Date thisSampleDate = new Date(mSamples.get(i).getTimestamp());
            Date nextSampleDate = new Date(mSamples.get(i + 1).getTimestamp());

            cal.setTime(thisSampleDate);
            Integer thisDay = cal.get(Calendar.DAY_OF_YEAR);

            currentListList.get(currentIndex).add(cal.get(Calendar.YEAR)
                    + "-" + cal.get(Calendar.MONTH)
                    + "-" + cal.get(Calendar.DAY_OF_MONTH)
                    + " " + cal.get(Calendar.HOUR_OF_DAY)
                    + ":" + cal.get(Calendar.MINUTE)
                    + "  " + mSamples.get(i).getValue()
                    + "  " + sensable.getUnit());

            cal.setTime(nextSampleDate);
            int nextDay = cal.get(Calendar.DAY_OF_YEAR);
            if (thisDay != nextDay) {
                cal.setTime(thisSampleDate);
                thisDayName = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
                listDataHeader.add(thisDayName);
                listDataChild.put(thisDayName, currentListList.get(currentIndex)); // Header, Child data
                currentListList.add(new ArrayList<String>());
            }
        }
        // Add final group
        thisDayName = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
        listDataHeader.add(thisDayName);
        listDataChild.put(thisDayName, currentListList.get(currentListList.size() - 1)); // Header, Child data

    }
}
