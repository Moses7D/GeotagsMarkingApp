package database;

import com.google.android.gms.maps.model.LatLng;

public class GeoMarker {

    public LatLng location;
    public String[] sensorData;
    public int colour;
    public String title;
    public String description;


    public GeoMarker() {
    }

    public GeoMarker(LatLng location, String[] sensorData) {
        this.location = location;
        this.sensorData = sensorData;
    }

    public GeoMarker(LatLng location, String[] sensorData, int colour, String title, String description) {
        this.location = location;
        this.sensorData = sensorData;
        this.colour = colour;
        this.title = title;
        this.description = description;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String[] getSensorData() {
        return sensorData;
    }

    public void setSensorData(String[] sensorData) {
        this.sensorData = sensorData;
    }

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
