package io.sensable.client.scheduler;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import io.sensable.SensableService;
import io.sensable.client.R;
import io.sensable.client.SensableUser;
import io.sensable.client.SensorHelper;
import io.sensable.client.sqlite.ScheduledSensablesTable;
import io.sensable.model.Sample;
import io.sensable.model.SampleResponse;
import io.sensable.model.SensableSender;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.List;

public class ScheduledSensableService extends Service {

    private static final String TAG = ScheduledSensableService.class.getSimpleName();

    private SensorManager sensorManager = null;
    private Sensor sensor = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Starting Service");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        ScheduleHelper scheduleHelper = new ScheduleHelper(this);
        Cursor cursor = scheduleHelper.getScheduledTasks();

        while (cursor.moveToNext()) {
            Log.d(TAG, "Adding one sampler");
            // Load the sensable from the DB
            final SensableSender sensableSender = ScheduledSensablesTable.getScheduledSensable(cursor);

            // Register the listener on the sensor
            List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
            Sensor sensor = sensorManager.getDefaultSensor(sensableSender.getInternalSensorId());
            sensorManager.registerListener(getListener(sensableSender), sensor, SensorManager.SENSOR_DELAY_NORMAL);

            // Mark this sensable as pending
            scheduleHelper.setSensablePending(sensableSender);
        }
        scheduleHelper.stopSchedulerIfNotNeeded();

        return START_STICKY;
    }

    private SensorEventListener getListener(final SensableSender sensableSender) {
        return new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Log.d(TAG, "Sensor Value Changed");
                ScheduleHelper scheduleHelper = new ScheduleHelper(ScheduledSensableService.this);

                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setEndpoint("http://sensable.io")
                        .build();
                SensableService service = restAdapter.create(SensableService.class);

                // Create the sample object
                Sample sample = new Sample();

                sample.setTimestamp((System.currentTimeMillis()));

                // This should parse the sensor type and format the values array differently
                sample.setValue(event.values[0]);

                /* Location */
                Location lastKnownLocation = getLocation();
                Log.d(TAG, "Location: " + lastKnownLocation.toString());

                // Update the sendable object
                sensableSender.setLocation(new double[]{lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude()});
                sensableSender.setSample(sample);
                sensableSender.setSensortype(event.sensor.getName());

                sensableSender.setInternalSensorId(event.sensor.getType());
                sensableSender.setUnit(SensorHelper.determineUnit(event.sensor.getType()));
                sensableSender.setPrivateSensor(false);
                sensableSender.setAccessToken(getUserAccessToken());

                Log.d(TAG, "Saving sample: " + event.sensor.getName() + " : " + event.values[0]);
                service.saveSample(sensableSender, new Callback<SampleResponse>() {
                    @Override
                    public void success(SampleResponse success, Response response) {
                        Log.d(TAG, "Success posting sample");
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Log.e(TAG, "Failed to post sample: " + retrofitError.toString());
                    }
                });

                scheduleHelper.unsetSensablePending(sensableSender);

                // stop the sensor and service
                sensorManager.unregisterListener(this);
                if (scheduleHelper.countPendingScheduledTasks() == 0) {
                    // Stop this service from sampling as we are not waiting for any more samples to come in
                    stopSelf();
                    scheduleHelper.stopSchedulerIfNotNeeded();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private String getUserAccessToken() {
        SensableUser user = new SensableUser(this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE), this);
        if (user.loggedIn) {
            if (user.hasAccessToken) {
                Log.d(TAG, "Loading Access token");
                Log.d(TAG, getString(R.string.preference_file_key));
                Log.d(TAG, getString(R.string.saved_access_token));
                SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
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

    private Location getLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        return locationManager.getLastKnownLocation(locationProvider);
    }


}
