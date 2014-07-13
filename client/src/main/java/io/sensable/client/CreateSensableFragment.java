package io.sensable.client;

import android.app.DialogFragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import io.sensable.model.Sample;
import io.sensable.model.Sensable;
import io.sensable.model.SensableSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madine on 01/07/14.
 */
public class CreateSensableFragment extends DialogFragment {

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
                    SensableSender sensableSender = new SensableSender();
                    Sensable sensable = new Sensable();
                    sensable.setSamples(new Sample[]{});
                    sensable.setSensorid(sensableId.getText().toString());
                    sensable.setLocation(new double[]{0,0});

                    sensable.setUnit(determineUnit(sensorSpinner.getSelectedItem().toString()));

                    sensableSender.setSensable(sensable);
                    sensableSender.setInterval(1);
                    createSensableListener.onConfirmed(sensableSender);
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), "Sensable ID is required", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    private String determineUnit(String sensorName) {
        Sensor chosenSensor = null;
        for(int i=0; i<sensorList.size(); i++){
            if(sensorList.get(i).getName() == sensorName) {
                chosenSensor = sensorList.get(i);
            }
        }

        if(chosenSensor == null) {
            return null;
        }

        String unit = "";
        switch(chosenSensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER: unit = "m/s^2";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD: unit = "uT";
                break;
            case Sensor.TYPE_GYROSCOPE: unit = "rad/s";
                break;
            case Sensor.TYPE_LIGHT: unit = "lux";
                break;
            case Sensor.TYPE_PRESSURE: unit = "hPa";
                break;
            case Sensor.TYPE_PROXIMITY: unit = "cm";
                break;
            case Sensor.TYPE_GRAVITY: unit = "m/s^2";
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION: unit = "m/s^2";
                break;
            case Sensor.TYPE_ROTATION_VECTOR: unit = "<θx, θy, θz>";
                break;
            case Sensor.TYPE_ORIENTATION: unit = "<degx, degy, degz>";
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY: unit = "%";
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE: unit = "°C";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED: unit = "uT";
                break;
            case Sensor.TYPE_GAME_ROTATION_VECTOR: unit = "<θx, θy, θz>";
                break;
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED: unit = "rad/s";
                break;
        }

        return unit;
    }

}
