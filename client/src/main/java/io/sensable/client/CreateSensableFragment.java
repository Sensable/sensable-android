package io.sensable.client;

import android.app.DialogFragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import io.sensable.model.SensableSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madine on 01/07/14.
 */
public class CreateSensableFragment extends DialogFragment {

    private List<Sensor> sensorList;
    private Spinner sensorSpinner;
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

        List<String> listSensorType = new ArrayList<String>();
        for(int i=0; i<sensorList.size(); i++){
            listSensorType.add(sensorList.get(i).getName());
        }

        sensorSpinner = (Spinner) view.findViewById(R.id.sensor_spinner);

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
                SensableSender sensableSender = new SensableSender();
                sensableSender.setSensable(sensorSpinner.getSelectedItem().toString());
                sensableSender.setInterval(1);
                createSensableListener.onConfirmed(sensableSender);
                dismiss();
            }

        });

    }

}
