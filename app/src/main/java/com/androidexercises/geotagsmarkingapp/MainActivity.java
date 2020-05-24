package com.androidexercises.geotagsmarkingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * TODO's for the whole App:
 *  * 01 make better UI for Location and Sensor Fragments, to give feedback whether the user can continue or not,
 *    in a graphical way.
 *  * 02 validate data before committing them to the database
 *  * 03 add an option to not customize the colour of the marker (probably button), although if the {@link com.madrapps.pikolo.HSLColorPicker}
 *    is not touched then the marker gets no custom colour, but the user is not aware of that.
 */

/**
 * Main activity that orchestrates the fragments.
 * The GUI is a forward liner navigation (with back-button functionality) for the user to input data for the marker.
 * Series of the fragments: {@link LocationFragment}, {@link SensorFragment}, {@link DescriptionFragment},
 * {@link ColourPickerFragment}, {@link FinalFragment}
 */
public class MainActivity extends AppCompatActivity implements ButtonHandler {
    // Tags to use on a {@link FragmentManager}
    public final static String FRAGMENTS_TAG_LOCATION_FRAGMENT = "com.androidexercises.geotagsmarkingapp.LocationFragment";
    public final static String FRAGMENTS_TAG_SENSOR_FRAGMENT = "com.androidexercises.geotagsmarkingapp.SensorFragment";
    public final static String FRAGMENTS_TAG_COLOUR_FRAGMENT = "com.androidexercises.geotagsmarkingapp.ColourPickerFragment";
    public final static String FRAGMENTS_TAG_DESCRIPTION_FRAGMENT = "com.androidexercises.geotagsmarkingapp.DescriptionFragment";
    public final static String FRAGMENTS_TAG_FINAL_FRAGMENT = "com.androidexercises.geotagsmarkingapp.FinalFragment";

    // Keys to be used on the Budle
    public final static String BUNDLE_KEYS_MARKER_LOCATION_LNG = "com.androidexercises.geotagsmarkingapp.GeoMarker.location.lng";
    public final static String BUNDLE_KEYS_MARKER_LOCATION_LAT = "com.androidexercises.geotagsmarkingapp.GeoMarker.location.lat";
    public final static String BUNDLE_KEYS_MARKER_SENSOR_CODE = "com.androidexercises.geotagsmarkingapp.GeoMarker.SensorFragment.code";
    public final static String BUNDLE_KEYS_MARKER_SENSOR_NAME = "com.androidexercises.geotagsmarkingapp.GeoMarker.SensorFragment.name";
    public final static String BUNDLE_KEYS_MARKER_SENSOR_MEASUREMENT = "com.androidexercises.geotagsmarkingapp.GeoMarker.SensorFragment.measurement";
    public final static String BUNDLE_KEYS_MARKER_COLOUR = "com.androidexercises.geotagsmarkingapp.GeoMarker.colour";
    public final static String BUNDLE_KEYS_MARKER_TITLE = "com.androidexercises.geotagsmarkingapp.GeoMarker.title";
    public final static String BUNDLE_KEYS_MARKER_DESCRIPTION = "com.androidexercises.geotagsmarkingapp.GeoMarker.description";

    public final static FirebaseFirestore databaseCloud = FirebaseFirestore.getInstance();

    //Bundle to fill with data as user navigates through the app
    private final Bundle markerData = new Bundle();
    private final LinkedHashMap<String, String[]> texts = new LinkedHashMap<>();

    private FragmentManager fragManager;
    private TextView title, details;
    private Button mainButton;
    /*
    flags used that mark what the main button is supposed to do when the final fragment is shown,
    also used to set the right texts ("Next","Commit","Again?")
    */
    private boolean toCommit, committed;

    private enum FragmentClasses {
        LocationFragment, SensorFragment, ColourPickerFragment, DescriptionFragment, FinalFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragManager = getSupportFragmentManager();
        title = findViewById(R.id.act_ttl);
        details = findViewById(R.id.act_dtl);
        mainButton = findViewById(R.id.act_btn_next);
        loadTexts();
        //Load all the fragments to the fragment manager if this is a new run, and set the right text
        if (findViewById(R.id.frag_cont) != null) {
            if (savedInstanceState != null) return;
            //All fragments once loaded are detached, except for the LocationFragment which is the first fragment
            title.setText(texts.get(FRAGMENTS_TAG_LOCATION_FRAGMENT)[0]);
            details.setText(texts.get(FRAGMENTS_TAG_LOCATION_FRAGMENT)[1]);
            Fragment frag = new SensorFragment();
            fragManager.beginTransaction().add(R.id.frag_cont, frag, FRAGMENTS_TAG_SENSOR_FRAGMENT).detach(frag).commit();
            frag = new DescriptionFragment();
            fragManager.beginTransaction().add(R.id.frag_cont, frag, FRAGMENTS_TAG_DESCRIPTION_FRAGMENT).detach(frag).commit();
            frag = new ColourPickerFragment();
            fragManager.beginTransaction().add(R.id.frag_cont, frag, FRAGMENTS_TAG_COLOUR_FRAGMENT).detach(frag).commit();
            frag = new FinalFragment();
            fragManager.beginTransaction().add(R.id.frag_cont, frag, FRAGMENTS_TAG_FINAL_FRAGMENT).detach(frag).commit();
            frag = new LocationFragment();
            fragManager.beginTransaction().add(R.id.frag_cont, frag, FRAGMENTS_TAG_LOCATION_FRAGMENT).attach(frag).commit();
        }
        toCommit = true;
        committed = false;
    }

    /**
     * Main button's click event handler
     * @param v
     */
    public void next(View v) {
        Fragment currentFragment = fragManager.findFragmentById(R.id.frag_cont);
        Log.i("fragments_main", "fragment found by id class: " + currentFragment.getClass().getSimpleName());
        Fragment newFrag;
        FragmentTransaction transaction = fragManager.beginTransaction();
        if (currentFragment != null) {
            //Get the fragment's class name as an enum value so it can be used in a switch-case statement
            FragmentClasses fragClass = FragmentClasses.valueOf(currentFragment.getClass().getSimpleName());
            switch (fragClass) {
                case LocationFragment:
                    //Current fragment: Location, Next fragment: Sensor
                    //taking data from the current fragment
                    LocationFragment lFrag = (LocationFragment) currentFragment;
                    markerData.putDouble(BUNDLE_KEYS_MARKER_LOCATION_LAT, lFrag.coordinates.latitude);
                    markerData.putDouble(BUNDLE_KEYS_MARKER_LOCATION_LNG, lFrag.coordinates.longitude);
                    Log.i("marker", "location set on geo-marker: " + lFrag.coordinates);
                    //setting the helper texts on the activity ui for the next fragment
                    title.setText(texts.get(FRAGMENTS_TAG_SENSOR_FRAGMENT)[0]);
                    details.setText(texts.get(FRAGMENTS_TAG_SENSOR_FRAGMENT)[1]);
                    //retrieving the next fragment from the manager and attaching it through a transaction
                    newFrag = fragManager.findFragmentByTag(FRAGMENTS_TAG_SENSOR_FRAGMENT);
                    transaction.hide(lFrag);
                    transaction.attach(newFrag);
                    transaction.show(newFrag);
                    transaction.addToBackStack(FRAGMENTS_TAG_SENSOR_FRAGMENT);
                    transaction.commit();
                    blockButton();
                    break;
                case SensorFragment:
                    //Current fragment: Sensor, Next fragment: Description
                    //taking data from the current fragment
                    SensorFragment sFrag = (SensorFragment) currentFragment;
                    markerData.putInt(BUNDLE_KEYS_MARKER_SENSOR_CODE, sFrag.chosenSensor.getTypeCode());
                    markerData.putString(BUNDLE_KEYS_MARKER_SENSOR_NAME, sFrag.chosenSensor.getSensorName());
                    markerData.putFloat(BUNDLE_KEYS_MARKER_SENSOR_MEASUREMENT, sFrag.measurement);
                    //setting the helper texts on the activity ui for the next fragment
                    title.setText(texts.get(FRAGMENTS_TAG_DESCRIPTION_FRAGMENT)[0]);
                    details.setText(texts.get(FRAGMENTS_TAG_DESCRIPTION_FRAGMENT)[1]);
                    //retrieving the next fragment from the manager and attaching it through a transaction
                    newFrag = fragManager.findFragmentByTag(FRAGMENTS_TAG_DESCRIPTION_FRAGMENT);
                    transaction.hide(sFrag);
                    transaction.attach(newFrag);
                    transaction.show(newFrag);
                    transaction.addToBackStack(FRAGMENTS_TAG_DESCRIPTION_FRAGMENT);
                    transaction.commit();
                    break;
                case DescriptionFragment:
                    //Current fragment: Description, Next fragment: ColourPicker
                    //taking data from the current fragment
                    DescriptionFragment dFrag = (DescriptionFragment) currentFragment;
                    markerData.putString(BUNDLE_KEYS_MARKER_TITLE, dFrag.title);
                    markerData.putString(BUNDLE_KEYS_MARKER_DESCRIPTION, dFrag.description);
                    Log.i("marker", "title set on geo-marker: " + dFrag.title);
                    Log.i("marker", "description set on geo-marker: " + dFrag.description);
                    //setting the helper texts on the activity ui for the next fragment
                    title.setText(texts.get(FRAGMENTS_TAG_COLOUR_FRAGMENT)[0]);
                    details.setText(texts.get(FRAGMENTS_TAG_COLOUR_FRAGMENT)[1]);
                    //retrieving the next fragment from the manager and attaching it through a transaction
                    newFrag = fragManager.findFragmentByTag(FRAGMENTS_TAG_COLOUR_FRAGMENT);
                    transaction.hide(dFrag);
                    transaction.attach(newFrag);
                    transaction.show(newFrag);
                    transaction.addToBackStack(FRAGMENTS_TAG_COLOUR_FRAGMENT);
                    transaction.commit();
                    break;
                case ColourPickerFragment:
                    //Current fragment: ColourPicker, Next fragment: Final
                    //taking data from the current fragment
                    ColourPickerFragment cFrag = (ColourPickerFragment) currentFragment;
                    markerData.putFloat(BUNDLE_KEYS_MARKER_COLOUR, cFrag.hue);
                    Log.i("marker", "colour set on geo-marker: " + cFrag.hue);
                    //setting the helper texts on the activity ui for the next fragment
                    title.setText(texts.get(FRAGMENTS_TAG_FINAL_FRAGMENT)[0]);
                    details.setText(texts.get(FRAGMENTS_TAG_FINAL_FRAGMENT)[1]);
                    //retrieving the next fragment from the manager and attaching it through a transaction
                    newFrag = fragManager.findFragmentByTag(FRAGMENTS_TAG_FINAL_FRAGMENT);
                    newFrag.setArguments(markerData);
                    transaction.hide(cFrag);
                    transaction.attach(newFrag);
                    transaction.show(newFrag);
                    transaction.addToBackStack(FRAGMENTS_TAG_FINAL_FRAGMENT);
                    transaction.commit();
                    if (committed) {
                        mainButton.setText(R.string.act_btn_str_again);
                    } else {
                        mainButton.setText(R.string.act_btn_str_commit);
                    }
                    break;
                case FinalFragment:
                    //TODO ACT ON WHETHER COMMISSION TO DATABASE WAS SUCCESSFUL OR NOT
                    if (toCommit) {
                        //Current fragment: FinalFragment, Next fragment: Final
                        //taking the current data to commit data to the database
                        FinalFragment fFrag = (FinalFragment) currentFragment;
                        committed = fFrag.commitToDatabase();
                        toCommit = !committed;
                        if (committed) {
                            mainButton.setText(R.string.act_btn_str_again);
                            Toast.makeText(getApplicationContext(), R.string.act_toast_pos, Toast.LENGTH_LONG);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.act_toast_neg, Toast.LENGTH_LONG);
                        }
                    } else {
                        //reset the fragments' variables
                        resetAppGUI();
                        //setting the helper texts on the activity ui for the next fragment
                        title.setText(texts.get(FRAGMENTS_TAG_LOCATION_FRAGMENT)[0]);
                        details.setText(texts.get(FRAGMENTS_TAG_LOCATION_FRAGMENT)[1]);
                        mainButton.setText(R.string.act_btn_str_next);
                        //retrieving the next fragment from the manager and attaching it through a transaction
                        newFrag = fragManager.findFragmentByTag(FRAGMENTS_TAG_LOCATION_FRAGMENT);
                        transaction.hide(currentFragment);
                        transaction.attach(newFrag);
                        transaction.show(newFrag);
                        transaction.commit();
                        toCommit = true;
                        committed = false;
                    }
                    break;
                default:
                    Log.i("click event", "cannot check");
            }
        }
        Log.i("click event", "clicked");
    }

    /**
     * GUI dynamically changes texts as user navigates the app, here the texts are loaded (from the xml file)
     * into a map that uses each fragment's tag as a key, so each time a fragment and it's GUi texts
     * can be found using it's tag.
     * Queue of the string-array on the xml must not be messed up, the line up is the same as the in
     * the navigation of the app (locationFragment, SensorFragment etc)
     */
    private void loadTexts() {
        String titles[] = getResources().getStringArray(R.array.titles);
        String details[] = getResources().getStringArray(R.array.details);
        texts.put(FRAGMENTS_TAG_LOCATION_FRAGMENT, new String[]{titles[0], details[0]});
        texts.put(FRAGMENTS_TAG_SENSOR_FRAGMENT, new String[]{titles[1], details[1]});
        texts.put(FRAGMENTS_TAG_DESCRIPTION_FRAGMENT, new String[]{titles[2], details[2]});
        texts.put(FRAGMENTS_TAG_COLOUR_FRAGMENT, new String[]{titles[3], details[3]});
        texts.put(FRAGMENTS_TAG_FINAL_FRAGMENT, new String[]{titles[4], details[4]});
    }

    /**
     * Resets the App's GUI so data for a new marker can be collected.
     */
    private void resetAppGUI() {
        blockButton();
        List<Fragment> fragments = fragManager.getFragments();
        Fragment frag;
        Resettable resettable;
        int i;
        //Iterate through all the fragments to reset them
        for (i = 0; i < fragments.size(); i++) {
            frag = fragments.get(i);
            resettable = (Resettable) frag;
            resettable.reset();
        }
        /*
        pop the back stack till it's empty so no back-button causes messes with the navigation
        this has the extra effect of bringing the first fragment LocationFragment, to the foreground
        and showing it, so the navigation starts anew.
         */
        for (i = 0; i < fragManager.getBackStackEntryCount(); ++i) {
            fragManager.popBackStack();
        }
    }

    @Override
    public void blockButton() {
        mainButton.setEnabled(false);
    }

    @Override
    public void unblockButton() {
        mainButton.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mainButton.setText(R.string.act_btn_str_next);
    }

    @Override
    /**
     * Permission requests' responses from {@link ActivityCompat#requestPermissions(Activity, String[], int)}
     * are headed here, this method is chosen instead of using {@link AppCompatActivity#requestPermissionsFromFragment(Fragment, String[], int)}
     * as a centralized way of managing the permissions requested and their responses.
     * At first App requests GPS location, if it is not given then requests Network location,
     * but if GPS location is given, then Network location is not requested
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("permissions", "request returned");
        LocationFragment lFrag = (LocationFragment)fragManager.findFragmentByTag(FRAGMENTS_TAG_LOCATION_FRAGMENT);
        switch (requestCode) {
            case LocationFragment.PERMISSION_REQUEST_FINE_LOCATION:
                //if permission for fine location (GPS) was given
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do what needed to with the GPS location
                    Log.i("permissions", "request in GPS permitted");
                    lFrag.manageLocationProcedures(Manifest.permission.ACCESS_FINE_LOCATION, LocationManager.GPS_PROVIDER);
                } else {
                    //Check if coarse location (Network Provider) is NOT permitted
                    Log.i("permissions", "request in GPS not permitted");
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //if not permitted request permission
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                LocationFragment.PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                }
                break;
            case LocationFragment.PERMISSION_REQUEST_COARSE_LOCATION:
                //if permission for coarse location (Network Provider) was given
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("permissions", "request in network permitted");
                    //do what needed to with the Network Provider location
                    lFrag.manageLocationProcedures(Manifest.permission.ACCESS_COARSE_LOCATION, LocationManager.NETWORK_PROVIDER);
                } else {
                    Log.i("permissions", "request in network not permitted");
                }
        }
    }
}
