package com.androidexercises.geotagsmarkingapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidexercises.geotagsmarkingapp.adaper_items.SpinnerItemSensor;

import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass, to draw data from the environment sensors of the phone (if any),
 * since this is a demo app no strict demand for the existence of any of the four (Light, Pressure,
 * Humidity, Light) is declared on the Manifest. If there is any of the four sensors then the app runs
 * on real mode, drawing actual data from the sensors. If none of the four sensors are present, then
 * the app goes into a made mode, named fake in the docs. On the fake mode, the class fills it's needed
 * tables with semi-dummy data, and the value is gotten randomly. The user chooses the sensor to draw data from
 * though a spinner which contains options for the available sensors.
 * TODO: make app to run in a hybrid state where any missing sensor is filled with a fake semi-dummy data entry,
 *  use a loop to iterate through all the sensors obtained with {@link Sensor#TYPE_ALL}, an enum value
 *  filled with the {@link Sensor#TYPE_AMBIENT_TEMPERATURE} etc constants, and a switch-case distilling
 *  the options, and filling the missing entries with semi-dummy data
 */
public class SensorFragment extends Fragment implements Resettable {
    public float measurement;
    public SpinnerItemSensor chosenSensor;

    private final Random r = new Random();

    private String[] sensorNames;
    private SensorManager sensorManager;
    private ButtonHandler buttonHandler;
    private Spinner spinner;
    private SpinnerItemSensor sensors[];
    private TextView measureTxt;

    //Interface instance to receive the sensors' events
    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        /**
         * Handles the new data taken from the event, but this method can be made called with null
         * event, when it's running in it's fake mode.
         */
        public void onSensorChanged(SensorEvent event) {
            //if not null then the app runs on real mode, if null then on fake mode.
            if(event != null)
                measurement = event.values[0];
            else
                measurement = r.nextFloat();
            measureTxt.setText(Float.toString(measurement));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //TODO
        }
    };

    public SensorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        buttonHandler = (ButtonHandler) context;
        buttonHandler.blockButton();
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorNames = getResources().getStringArray(R.array.sensor_names);
        //Attempt to fill the sensor table
        populateSensorTable();
        //If not even a single sensor is found fake fill the sensor table
        if(sensors[0] == null)
            populateSensorTableFAKE();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sensor, container, false);
        measureTxt = v.findViewById(R.id.sns_measure_txt);
        spinner = v.findViewById(R.id.sns_spinner);
        //sets the adapter data(sensors), the chosen item layout and the dropdown list of the spinner layout
        ArrayAdapter<SpinnerItemSensor> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sensors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //When a new sensor is chosen the text-field showing the value resets and the "next" button is blocked
                measureTxt.setText("");
                buttonHandler.blockButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //onClickListener for the button, the user presses the button when he wants to take a snapshot of the data.
        (v.findViewById(R.id.sns_btn_measure)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO TAKE MEASUREMENT
                chosenSensor = (SpinnerItemSensor) spinner.getSelectedItem();
                //If sensor is null it means there is no sensor, so fake measurements will be taken
                if(chosenSensor.getSensor() != null)
                    sensorManager.registerListener(sensorListener,chosenSensor.getSensor(),Sensor.REPORTING_MODE_ONE_SHOT);
                else
                    sensorListener.onSensorChanged(null);
                buttonHandler.unblockButton();
            }
        });
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.unregisterListener(sensorListener);
    }

    /**
     * Populates the needed array with the available sensors, the table is used to show the options on
     * the spinner but also to draw the data from the sensor, once a sensor is chosen and the button is pressed.
     */
    private void populateSensorTable() {
        sensors = new SpinnerItemSensor[4];
        Sensor sensor;
        int index = 0;
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (sensor != null) {
            sensors[index] = new SpinnerItemSensor(Sensor.TYPE_LIGHT, sensorNames[1], sensor);
            index++;
        }
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if (sensor != null) {
            sensors[index] = new SpinnerItemSensor(Sensor.TYPE_PRESSURE, sensorNames[3], sensor);
            index++;
        }
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        if (sensor != null) {
            sensors[index] = new SpinnerItemSensor(Sensor.TYPE_RELATIVE_HUMIDITY, sensorNames[2], sensor);
            index++;
        }
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        if (sensor != null) {
            sensors[index] = new SpinnerItemSensor(Sensor.TYPE_AMBIENT_TEMPERATURE, sensorNames[0], sensor);
            index++;
        }
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (int i = 0; i < sensors.size(); i++) {
            Log.i("fragments_sensor", "name: " + sensors.get(i).getName() + " type: " + sensors.get(i).getType());
        }
    }

    /**
     * Fake fills the table of sensors that can be used, all values for GUI purposes are set, only the actual sensor objects are null
     * and so they will not be used. Fake measurement will be taken.
     */
    private void populateSensorTableFAKE() {
        sensors = new SpinnerItemSensor[4];
        int index = 0;
        sensors[index] = new SpinnerItemSensor(Sensor.TYPE_LIGHT, sensorNames[1], null);
        index++;
        sensors[index] = new SpinnerItemSensor(Sensor.TYPE_PRESSURE, sensorNames[3], null);
        index++;
        sensors[index] = new SpinnerItemSensor(Sensor.TYPE_RELATIVE_HUMIDITY, sensorNames[2], null);
        index++;
        sensors[index] = new SpinnerItemSensor(Sensor.TYPE_AMBIENT_TEMPERATURE, sensorNames[0], null);

    }

    @Override
    public void reset() {
        measurement = -1.0f;
        chosenSensor = null;
        spinner.setSelection(0);
    }
}
