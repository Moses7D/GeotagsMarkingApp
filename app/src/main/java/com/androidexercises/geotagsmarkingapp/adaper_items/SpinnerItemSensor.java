package com.androidexercises.geotagsmarkingapp.adaper_items;

import android.hardware.Sensor;

import androidx.annotation.NonNull;

/**
 * A POJO class to be hold in the spinner on the GUI where the user chooses the sensor to draw data from.
 * It's more convenient since it holds other data the app needs, and with the simple use of the {@link SpinnerItemSensor#toString()}
 * the adapter shows the desired string, but still the whole instance is obtainable from the spinner
 * with the accompanying data.
 */
public class SpinnerItemSensor {
    private int typeCode; //value of the Sensor class constants TYPE_<sensor>
    private String sensorName; //sensor name to show
    private Sensor sensor;

    public SpinnerItemSensor() {
    }

    public SpinnerItemSensor(int typeCode, String sensorName, Sensor sensor) {
        this.typeCode = typeCode;
        this.sensorName = sensorName;
        this.sensor = sensor;
    }

    public int getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(int typeCode) {
        this.typeCode = typeCode;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    @NonNull
    @Override
    public String toString() {
        return sensorName;
    }
}
