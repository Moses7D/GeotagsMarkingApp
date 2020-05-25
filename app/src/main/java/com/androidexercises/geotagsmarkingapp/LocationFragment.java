package com.androidexercises.geotagsmarkingapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;


/**
 * A simple {@link Fragment} subclass, to obtain the user's location. Class manages it's needs for
 * permissions and enabling the location finder service (GPS). Two methods are set to obtain the location,
 * one through location updates when the user is moving with the GPS open, and a second one through
 * the "find my location" button on the map.
 */
public class LocationFragment extends Fragment implements Resettable {
    /**
     * When requesting a permission with {@link ActivityCompat#requestPermissions(Activity, String[], int)}
     * or any other requestPermissions method custom a flag -as a positive int- must be given as a
     * parameter to return when results return. These are the flags.
     */
    public static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 2;

    /*
    private Geocoder geocoder;
    can be used to create suggestions for the location's title and description
     */
    public LatLng coordinates;

    private MapView mapView;
    private GoogleMap gMap;
    private LocationManager locManager;
    private ButtonHandler buttonHandler;

    public LocationFragment() {
        // Required empty public constructor
        Log.i("fragments_location", "constructor called");

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        buttonHandler = (ButtonHandler) context;
        Log.i("fragments_location", "onAttach called");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("fragments_location", "onCreate called");
        //geocoder = new Geocoder(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("fragments_location", "onCreateView called");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_location, container, false);
        mapView = v.findViewById(R.id.loc_map_view);
        mapView.onCreate(savedInstanceState);
        //needed to get a GoogleMap instance
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LocationFragment.this.onMapReady(googleMap);
            }
        });
        buttonHandler.blockButton();
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onResume();
    }

    @Override
    public void onResume() {
        Log.i("fragments_location", "onResume called");
        super.onResume();
        mapView.onResume();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LocationFragment.this.onMapReady(googleMap);
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.i("fragments_location", "onDestroy called");
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        Log.i("fragments_location", "onLowMemory called");
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void onMapReady(GoogleMap googleMap) {
        Log.i("fragments_location", "init of google map");
        gMap = googleMap;
        //Initializes the location manager, sub-procedures and requests location permission, GPS or Network, to do so
        /*
        It is run after google map is initialized, securing that the sub-procedures (referring to the google
        map as well) will be done safely
        */
        this.initLocManager();
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSPromptDialogue();
        } else {

        }
    }

    /**
     * Initializes the {@link LocationManager} to be used from this fragment, and starts a series of events
     * to enable the feature of getting the user's location
     */
    private void initLocManager() {
        locManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
        //Check if fine location (GPS) is NOT permitted
        Log.i("permissions", "checking if permitted");
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("permissions", "ACCESS_FINE_LOCATION not permitted");
            //if not permitted request permission
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_FINE_LOCATION);
        } else {
            //if permitted then enable features to get the user's location
            manageLocationProcedures(Manifest.permission.ACCESS_FINE_LOCATION, LocationManager.GPS_PROVIDER);
            Log.i("permissions", "permitted");
        }
    }

    /**
     * Sets the needed features and events that will take place, that are:
     * * Draws the "find my location" button on the map (feature)
     * * Sets the request for location updates (event)
     * * Sets the click events for "find my location" and "my location(blue dot)"
     *
     * @param permissionType Permission type to check upon if given, value must be of {@link Manifest.permission} types.
     *                       Must match that of providerType, e.g. {@link Manifest.permission#ACCESS_FINE_LOCATION}
     *                       for {@link LocationManager#GPS_PROVIDER}
     * @param providerType   Provider type to request updates from, must be of {@link LocationManager} providers
     */
    public void manageLocationProcedures(String permissionType, String providerType) {
        setMyLocationButton();
        setLocationUpdates(permissionType, providerType);
        setMyLocationClickEvents();
    }


    /**
     * Sets the "find my location" button on the map, enables the feature by altering certain flags
     * and initializes the map
     */
    private void setMyLocationButton() {
        Log.i("location", "my location button set");
        //Find my location button
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        //My location button "blue dot" on the map
        gMap.setMyLocationEnabled(true);
        MapsInitializer.initialize(getActivity());
    }

    /**
     * Sets the request for location updates for the given provider.
     *
     * @param permissionType Permission type to check upon if given, value must be of {@link Manifest.permission} types
     * @param providerType   Provider type to request updates from, must be of {@link LocationManager} providers
     */
    private void setLocationUpdates(String permissionType, String providerType) {
        Log.i("location", providerType + " location");
        /**
         * To be here it means that the permission checked is granted (since only successful requests call this method,
         * but android requires that before every call of {@link LocationManager#requestLocationUpdates(String, long, float, LocationListener)}
         * a check must be made.
         */
        if (ActivityCompat.checkSelfPermission(this.getContext(), permissionType) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locManager.requestLocationUpdates(providerType, 2000, 5, new LocationListener() {
            @Override
            /**
             * Transforms the {@link Location} object to a {@link LatLng} object so the user's
             * coordinates are saved and then zooms above the location. Since the location of the user
             * (required) is obtained then the button to move to the next fragment is unblocked.
             * Event triggers when the location of the user is changed or the asked time limit has passed.
             */
            public void onLocationChanged(Location location) {
                Log.i("location", "location changed");
                coordinates = new LatLng(location.getLatitude(), location.getLongitude());
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 10));
                buttonHandler.unblockButton();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    /**
     * Sets the on click events for "find my location" and "my location(blue dot)"
     */
    private void setMyLocationClickEvents() {
        Log.i("location", "setting on my location");
        gMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
            @Override
            /**
             * Transforms the {@link Location} object to a {@link LatLng} object so the user's
             * coordinates are saved and then zooms above the location. Since the location of the user
             * (required) is obtained then the button to move to the next fragment is unblocked. Event triggers when the
             * "my location (blue dot)" is pressed on the map.
             */
            public void onMyLocationClick(@NonNull Location location) {
                Log.i("location", "blue dot clicked");
                coordinates = new LatLng(location.getLatitude(), location.getLongitude());
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 10));
                buttonHandler.unblockButton();
            }
        });

        gMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            /**
             * Transforms the {@link Location} object to a {@link LatLng} object so the user's
             * coordinates are saved and then zooms above the location. Since the location of the user
             * (required) is obtained then the button to move to the next fragment is unblocked.
             * But the action is performed by getting the user's current location from the {@link GoogleMap}
             * object, which might be null. When null it is checked if the location is disabled (from the settings)
             * if that is then the user is prompted to a dialogue.
             * Event triggers when the "my location (blue dot)" is pressed on the map.
             */
            public boolean onMyLocationButtonClick() {
                Log.i("location", "my location button invoked");
                /**
                 * {@link GoogleMap#getMyLocation()} method is deprecated and the use of com.google.android.gms.location.FusedLocationProviderApi
                 * is recommended, but this needs extra configuration and provides more features that will not be needed for this app.
                 * Simply put too much effort and extra features that won't be even used.
                 */
                Location location = gMap.getMyLocation();
                if (location != null) {
                    Log.i("location", "google map my location returned: " + location);
                    coordinates = new LatLng(location.getLatitude(), location.getLongitude());
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 10));
                    buttonHandler.unblockButton();
                    return true;
                } else {
                    //Check if the location is disabled from the settings.
                    if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        //prompt to dialogue
                        showGPSPromptDialogue();
                    }
                    buttonHandler.blockButton();
                }
                return false;
            }
        });
    }

    /**
     * A dialogue to ask the user to enable the location services, leads to the settings
     */
    private void showGPSPromptDialogue() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.loc_dialogue_txt)
                .setCancelable(false)
                .setPositiveButton(R.string.loc_ans_pos, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.loc_ans_neg, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void reset() {
        coordinates = null;
        //TODO Zoom-out and change the coordinates the map is showing to a default
    }
}
