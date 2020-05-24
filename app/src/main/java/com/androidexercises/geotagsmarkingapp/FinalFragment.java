package com.androidexercises.geotagsmarkingapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import database.GeoMarker;


/**
 * A simple {@link Fragment} subclass, to show the data collected for final inspection by the user,
 * before they send them to the server. The data are send from the {@link MainActivity} to this fragment
 * through a {@link Bundle} using the keys of the Activity, then the bundle is unpacked into a {@link GeoMarker} and
 * a {@link MarkerOptions} object. In this GUI the data collected and the description/title, are shown
 * on different parts of the interface, even thought it would make more sense to pack them under a Marker's
 * {@link Marker#setTitle(String)} and {@link Marker#setSnippet(String)}, but this page is for inspection,
 * so everything is put on it's own place.
 * This fragment doesn't have a button of it's own to click when the user wants to commit the data,
 * the {@link MainActivity}'s button is used.
 */
public class FinalFragment extends Fragment implements Resettable {

    private final GeoMarker geoMarker = new GeoMarker();

    private MapView mapView;
    private TextView descTxt, typeTxt, measureTxt;
    private GoogleMap gMap;
    private LatLng coordinates;
    private MarkerOptions markerOptions;
    private Marker marker;
    private String sensorName;

    public FinalFragment() {
        //Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_final, container, false);
        if (getArguments() != null)
            loadData();
        mapView = v.findViewById(R.id.fn_map_view);
        mapView.onCreate(savedInstanceState);
        descTxt = v.findViewById(R.id.fn_desc_txt);
        /*
        title and description can be null, if null string are passed to a text-field then "null" will
        show on the UI, so instead empty strings are given.
        */
        descTxt.setText(String.format(getString(R.string.fn_desc),
                ((geoMarker.title == null) ? "" : geoMarker.title),
                ((geoMarker.description == null) ? "" : geoMarker.description)));
        typeTxt = v.findViewById(R.id.fn_sns_type_txt);
        typeTxt.setText(sensorName);
        measureTxt = v.findViewById(R.id.fn_sns_measure_txt);
        measureTxt.setText(Float.toString(geoMarker.sensorMeasurement));
        //needed to get a GoogleMap instance
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                initGMap(googleMap);
            }
        });
        return v;
    }

    private void initGMap(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
        gMap.setMyLocationEnabled(false);
        MapsInitializer.initialize(getActivity());
        marker = gMap.addMarker(markerOptions);
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 10));
    }

    /**
     * Used to get a default marker look with customized colour.
     * @param hue the hue of the colour
     * @return a default marker look coloured by the given colour
     */
    private BitmapDescriptor gerColouredMarker(float hue) {
        return BitmapDescriptorFactory.defaultMarker(hue);
    }

    public boolean commitToDatabase() {
        MainActivity.databaseCloud.collection("markers").document().set(geoMarker);
        //TODO CHECK IF COMMISSION TO DATABASE WAS SUCCESSFUL
        return true;
    }

    /**
     * Loads the data taken from the Bundle into the {@link GeoMarker} and {@link MarkerOptions} objects.
     */
    private void loadData() {
        Bundle markerData = getArguments();
        coordinates = new LatLng(markerData.getDouble(MainActivity.BUNDLE_KEYS_MARKER_LOCATION_LAT), markerData.getDouble(MainActivity.BUNDLE_KEYS_MARKER_LOCATION_LNG));
        geoMarker.location = coordinates;
        geoMarker.hue = markerData.getFloat(MainActivity.BUNDLE_KEYS_MARKER_COLOUR);
        geoMarker.title = markerData.getString(MainActivity.BUNDLE_KEYS_MARKER_TITLE);
        geoMarker.description = markerData.getString(MainActivity.BUNDLE_KEYS_MARKER_DESCRIPTION);
        geoMarker.typeCode = markerData.getInt(MainActivity.BUNDLE_KEYS_MARKER_SENSOR_CODE);
        geoMarker.sensorMeasurement = markerData.getFloat(MainActivity.BUNDLE_KEYS_MARKER_SENSOR_MEASUREMENT);
        markerOptions = new MarkerOptions()
                .position(coordinates)
                .title(geoMarker.title)
                .icon(gerColouredMarker(geoMarker.hue));
        sensorName = markerData.getString(MainActivity.BUNDLE_KEYS_MARKER_SENSOR_NAME);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void reset() {
        marker.remove();
        marker = null;
        markerOptions = null;
        coordinates = null;
        geoMarker.clear();
        //TODO Zoom-out and change the coordinates the map is showing to a default
    }
}
