package io.sensable.client;

import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import io.sensable.client.scheduler.ScheduleHelper;
import io.sensable.client.sqlite.SavedSensablesTable;
import io.sensable.client.sqlite.SensableContentProvider;
import io.sensable.model.Sample;
import io.sensable.model.Sensable;
import io.sensable.model.SensableSender;

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
        public void onConfirmed(SensableSender sensableSender);
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
        for(int i=0; i<sensorList.size(); i++){
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

    public CreateSensableListener getRecipientPickerListener() {
        return createSensableListener;
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
                if(sensableId.getText().toString().length() > 0) {

                    int sensorId = getSensorId(sensorSpinner.getSelectedItem().toString());

                    // Create the object for scheduling
                    SensableSender sensableSender = new SensableSender();
                    sensableSender.setSensorid(sensableId.getText().toString());
                    sensableSender.setInternalSensorId(sensorId);
                    sensableSender.setSensortype(sensorSpinner.getSelectedItem().toString());
                    sensableSender.setUnit(SensorHelper.determineUnit(sensorId));
                    sensableSender.setPending(0);

                    //Create the bookmarkable object
                    Sensable sensable = new Sensable();
                    sensable.setSamples(new Sample[]{});
                    sensable.setSensorid(sensableId.getText().toString());
                    sensable.setLocation(new double[]{0,0});
                    sensable.setUnit(sensableSender.getUnit());

                    // Schedule the sensable
                    createScheduledSensable(sensableSender, sensable);

                    createSensableListener.onConfirmed(sensableSender);

                    dismiss();
                } else {
                    Toast.makeText(getActivity(), "Sensable ID is required", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    private boolean createScheduledSensable(SensableSender sensableSender, Sensable sensable) {
        ScheduleHelper scheduleHelper = new ScheduleHelper(getActivity());

        Log.d(TAG, "Creating Scheduler");

        // Try to create schedule entry
        if(!scheduleHelper.addSensableToScheduler(sensableSender)) {
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

        if(chosenSensor == null) {
            return -1;
        }
        return chosenSensor.getType();
    }


}
