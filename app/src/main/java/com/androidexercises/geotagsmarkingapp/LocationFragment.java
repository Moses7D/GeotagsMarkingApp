package com.androidexercises.geotagsmarkingapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


/**
 *
 */
public class LocationFragment extends Fragment {


    public static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 2;

    private MapView mapView;
    private GoogleMap gMap;
    private LocationManager locManager;
    private Geocoder geocoder;
    public LatLng coordinates;

    public LocationFragment() {
        Log.i("fragments",getClass().getName() + " constructor called");
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("fragments",getClass().getName() + " onCreateView called");
        View v = inflater.inflate(R.layout.fragment_location, container, false);
        mapView = v.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                initGMap(googleMap);
            }
        });
        geocoder = new Geocoder(getContext());
        this.initLocManager();
        return v;
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

    private void initGMap(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        gMap.setMyLocationEnabled(true);
        MapsInitializer.initialize(getActivity());
    }

    private void initLocManager() {
        locManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
        //Check if fine location (GPS) is NOT permitted
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //if not permitted request permission
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_FINE_LOCATION);
            return;
        }

    }

    @Override
    /**
     * At first App requests GPS location, if it is not given then requests Network location,
     * but if GPS location is given, then Network location is not requested
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_FINE_LOCATION:
                //if permission for fine location (GPS) was given
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do what needed to with the GPS location
                    this.setLocationUpdatesGPS();
                } else {
                    //Check if coarse location (Network Provider) is NOT permitted
                    if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //if not permitted request permission
                        ActivityCompat.requestPermissions(this.getActivity(),
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                        return;
                    }
                }
                break;
            case PERMISSION_REQUEST_COARSE_LOCATION:
                //if permission for coarse location (Network Provider) was given
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do what needed to with the Network Provider location
                    this.setLocationUpdatesNetwork();
                } else {
                }

        }
        //
    }

    private void setLocationUpdatesGPS() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    coordinates = new LatLng(location.getLatitude(), location.getLongitude());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1);
                        gMap.addMarker(new MarkerOptions()
                                .position(coordinates)
                                .title(addresses.get(0).getLocality() +
                                        addresses.get(0).getCountryName()));

                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 10));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

    private void setLocationUpdatesNetwork() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    coordinates = new LatLng(location.getLatitude(), location.getLongitude());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1);
                        gMap.addMarker(new MarkerOptions()
                                .position(coordinates)
                                .title(addresses.get(0).getLocality() +
                                        addresses.get(0).getCountryName()));

                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 10));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
    }
}
