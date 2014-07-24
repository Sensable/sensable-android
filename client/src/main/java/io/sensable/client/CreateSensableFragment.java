package io.sensable.client;

import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import io.sensable.SensableService;
import io.sensable.client.scheduler.ScheduleHelper;
import io.sensable.client.sqlite.SavedSensablesTable;
import io.sensable.client.sqlite.SensableContentProvider;
import io.sensable.model.Sample;
import io.sensable.model.SampleResponse;
import io.sensable.model.Sensable;
import io.sensable.model.ScheduledSensable;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madine on 01/07/14.
 */
public class CreateSensableFragment extends DialogFragment {

    private static final String TAG = CreateSensableFragment.class.getSimpleName();

    private List<Sensor> sensorList;
    private Spinner sensorSpinner;
    private EditText sensableId;
    private Button submitButton;
    private CreateSensableListener createSensableListener;

    public static interface CreateSensableListener {
        public void onConfirmed(ScheduledSensable scheduledSensable);
    }

    public CreateSensableFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        View view = inflater.inflate(R.layout.create_sensable_layout, null);

        getDialog().setTitle(getActivity().getString(R.string.dialogTitleCreateSensable));

        List<String> listSensorType = new ArrayList<String>();
        for (int i = 0; i < sensorList.size(); i++) {
            listSensorType.add(sensorList.get(i).getName());
        }

        sensorSpinner = (Spinner) view.findViewById(R.id.sensor_spinner);

        sensableId = (EditText) view.findViewById(R.id.create_sensable_id);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, listSensorType);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sensorSpinner.setAdapter(spinnerArrayAdapter);

        addListenerOnButton(view);

        return view;
    }

    public void setCreateSensableListener(CreateSensableListener createSensableListener) {
        this.createSensableListener = createSensableListener;
    }

    public void addListenerOnButton(View view) {

        sensorSpinner = (Spinner) view.findViewById(R.id.sensor_spinner);

        submitButton = (Button) view.findViewById(R.id.create_sensable_button);

        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sensableId.getText().toString().length() > 0) {

                    int sensorId = getSensorId(sensorSpinner.getSelectedItem().toString());

                    // Create the object for scheduling
                    final ScheduledSensable scheduledSensable = new ScheduledSensable();
                    scheduledSensable.setSensorid(sensableId.getText().toString());
                    scheduledSensable.setInternalSensorId(sensorId);
                    scheduledSensable.setSensortype(sensorSpinner.getSelectedItem().toString());
                    scheduledSensable.setUnit(SensorHelper.determineUnit(sensorId));
                    scheduledSensable.setPending(0);

                    Location lastKnownLocation = getLocation();

                    //Create the bookmarkable object
                    final Sensable sensable = new Sensable();
                    sensable.setSensorid(sensableId.getText().toString());
                    sensable.setUnit(scheduledSensable.getUnit());
                    sensable.setName(sensableId.getText().toString());
                    sensable.setSensortype(sensorSpinner.getSelectedItem().toString());
                    sensable.setLocation(new double[]{lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude()});
                    sensable.setSample(new Sample());
                    sensable.setAccessToken(getUserAccessToken());

                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setLogLevel(RestAdapter.LogLevel.FULL)
                            .setEndpoint("http://sensable.io")
                            .build();

                    SensableService service = restAdapter.create(SensableService.class);

                    service.createSensable(sensable, new Callback<SampleResponse>() {
                        @Override
                        public void success(SampleResponse sampleResponse, Response response) {
                            Log.d(TAG, "Callback Success: " + sampleResponse.getMessage());

                            // The service returns the canonical sensableID. Set that before saving the objects.
                            sensable.setSensorid(sampleResponse.getSensorid());
                            scheduledSensable.setSensorid(sampleResponse.getSensorid());

                            // Schedule the sensable then create the favourite bookmark
                            createScheduledSensable(scheduledSensable, sensable);
                            createSensableListener.onConfirmed(scheduledSensable);
                            dismiss();
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            Log.e(TAG, "Callback failure: " + retrofitError.toString());
                            Toast.makeText(getActivity(), "Could not create that Sensable", Toast.LENGTH_SHORT).show();
                        }
                    });


                } else {
                    Toast.makeText(getActivity(), "Sensable ID is required", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    private String getUserAccessToken() {
        SensableUser user = new SensableUser(getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE), getActivity());
        if (user.loggedIn) {
            if (user.hasAccessToken) {
                Log.d(TAG, "Loading Access token");
                Log.d(TAG, getString(R.string.preference_file_key));
                Log.d(TAG, getString(R.string.saved_access_token));
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                String username = sharedPreferences.getString(getString(R.string.saved_username), "");
                Log.d(TAG, "Username: " + username);
                String accessToken = sharedPreferences.getString(getString(R.string.saved_access_token), "");
                Log.d(TAG, "Access Token: " + accessToken);
                return accessToken;
            } else {
                Log.d(TAG, "No access Token");
                return "";
            }
        } else {
            Log.d(TAG, "Not logged in");
            return "";
        }

    }


    private boolean createScheduledSensable(ScheduledSensable scheduledSensable, Sensable sensable) {
        ScheduleHelper scheduleHelper = new ScheduleHelper(getActivity());

        Log.d(TAG, "Creating Scheduler");

        // Try to create schedule entry
        if (!scheduleHelper.addSensableToScheduler(scheduledSensable)) {
            return false;
        }

        //Give the scheduler a kick in case it isn't already running.
        scheduleHelper.startScheduler();

        // Try to create local bookmark to this sensable
        return saveSensable(sensable);

    }

    private boolean saveSensable(Sensable sensable) {
        ContentValues mNewValues = SavedSensablesTable.serializeSensableForSqlLite(sensable);

        Uri mNewUri = getActivity().getContentResolver().insert(
                SensableContentProvider.CONTENT_URI,   // the user dictionary content URI
                mNewValues                          // the values to insert
        );
        return true;
    }

    private int getSensorId(String sensorName) {
        Sensor chosenSensor = null;
        for (Sensor aSensorList : sensorList) {
            if (aSensorList.getName().equals(sensorName)) {
                chosenSensor = aSensorList;
            }
        }

        if (chosenSensor == null) {
            return -1;
        }
        return chosenSensor.getType();
    }

    private Location getLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        return locationManager.getLastKnownLocation(locationProvider);
    }


}
