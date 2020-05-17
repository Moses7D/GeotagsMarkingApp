package com.androidexercises.geotagsmarkingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;

import database.GeoMarker;

public class MainActivity extends AppCompatActivity {
    public final static String FRAGMENTS_TAG_LOCATION_FRAGMENT = "com.androidexercises.geotagsmarkingapp.LocationFragment";
    public final static String FRAGMENTS_TAG_SENSOR_FRAGMENT = "com.androidexercises.geotagsmarkingapp.SensorFragment";
    public final static String FRAGMENTS_TAG_COLOUR_FRAGMENT = "com.androidexercises.geotagsmarkingapp.ColourPickerFragment";
    //public final static String FRAGMENTS_TAG_DESCRIPTION_FRAGMENT = "com.androidexercises.geotagsmarkingapp.LocationFragment";

    public final static String BUNDLE_KEYS_MARKER_COLOUR = "com.androidexercises.geotagsmarkingapp.GeoMarker.colour";
    public final static String BUNDLE_KEYS_MARKER_LOCATION = "com.androidexercises.geotagsmarkingapp.GeoMarker.location";
    public final static String BUNDLE_KEYS_MARKER_DESCRIPTION = "com.androidexercises.geotagsmarkingapp.GeoMarker.description";
    public final static String BUNDLE_KEYS_MARKER_TITLE = "com.androidexercises.geotagsmarkingapp.GeoMarker.title";


    public FirebaseFirestore databaseCloud;
    public FragmentManager fragManager;
    private final GeoMarker marker = new GeoMarker();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseCloud = FirebaseFirestore.getInstance();
        fragManager = getSupportFragmentManager();

        Log.i("fragments", "not in");
        if(findViewById(R.id.frag_cont) != null) {
            Log.i("fragments", "in");
            if(savedInstanceState != null) return;
            Log.i("fragments", "setting");
            LocationFragment locFrag = new LocationFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frag_cont, locFrag, FRAGMENTS_TAG_LOCATION_FRAGMENT).commit();
        }
    }

    public void next(View v){
        Fragment frag = fragManager.findFragmentById(R.id.frag_cont);
        FragmentTransaction transaction = fragManager.beginTransaction();
        if(frag != null) {
            FragmentClasses fragClass = FragmentClasses.valueOf(frag.getClass().getSimpleName());
            switch (fragClass) {
                case LocationFragment:
                    marker.setLocation(((LocationFragment)frag).coordinates);
                    Log.i("geomarker", "location set on geomarker: " +marker.location);
                    frag = new SensorFragment();
                    transaction.replace(R.id.frag_cont,frag,FRAGMENTS_TAG_SENSOR_FRAGMENT);
                    transaction.addToBackStack(FRAGMENTS_TAG_LOCATION_FRAGMENT);
                    transaction.commit();

                    break;
                case SensorFragment:
                    //TODO add sensor data to the GeoMarker Obj
                    frag = new ColourPickerFragment();
                    transaction.replace(R.id.frag_cont,frag,FRAGMENTS_TAG_COLOUR_FRAGMENT);
                    transaction.addToBackStack(FRAGMENTS_TAG_COLOUR_FRAGMENT);
                    transaction.commit();
                    break;
                case ColourPickerFragment:
                    marker.setColour(((ColourPickerFragment)frag).colour);
                    Log.i("geomarker", "colour set on geomarker: " + marker.colour);
                    //TODO add description input fragment
                    /*frag = new SensorFragment();
                    transaction.replace(R.id.frag_cont,frag,FRAGMENTS_TAG_SENSOR_FRAGMENT);
                    transaction.addToBackStack(FRAGMENTS_TAG_LOCATION_FRAGMENT);
                    transaction.commit();
                     */
                    break;
                default:
                    Log.i("click event", "cannot check");
            }
        }
        Log.i("click event","clicked");

    }

    private enum FragmentClasses{
        LocationFragment,SensorFragment, ColourPickerFragment;
    }
}
