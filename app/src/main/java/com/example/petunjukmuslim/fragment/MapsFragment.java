package com.example.petunjukmuslim.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.petunjukmuslim.MainActivity;
import com.example.petunjukmuslim.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.petunjukmuslim.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    boolean isPermissionGranted;
    private GoogleMap mMap;
    private MainActivity binding;
    SupportMapFragment mapFragment;
    TextView txtDistance;
    private FusedLocationProviderClient client;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            // Add a marker in Sydney and move the camera
            //3.206972843097494, 101.68101170482517

            //21.422637698095706, 39.82619708312029
            LatLng mekah = new LatLng(21.422637698095706, 39.82619708312029);
            mMap.addMarker(new MarkerOptions().position(mekah).title("Marker in mekah"));

            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mekah,15));
            //mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        } **/



        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        client = LocationServices.getFusedLocationProviderClient(((MapsFragment) this).getActivity());

        if (ActivityCompat.checkSelfPermission(((MapsFragment) this).getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {

                                mMap = googleMap;

                                double localat =location.getLatitude();
                                double localong = location.getLongitude();

                                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                                MarkerOptions options = new MarkerOptions().position(latLng).title("You're here");

                                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                                //mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.addMarker(options);

                                //add mekah marker
                                LatLng mekah = latMekah();
                                MarkerOptions mekahmarker = new MarkerOptions().position(mekah).title("Makkah");
                                mMap.addMarker(mekahmarker);


                                double mekahlat = getMekahLat();
                                double mekahlong = getMekahLong();

                                getDistance(mekahlat,mekahlong,localat,localong);

                                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                builder.include(options.getPosition());
                                builder.include(mekahmarker.getPosition());

                                LatLngBounds bounds = builder.build();

                                int padding = 200; // offset from edges of the map in pixels
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                                mMap.animateCamera(cu);



                            }
                        });
                    }
                }
            });

        }else{
            ActivityCompat.requestPermissions(((MapsFragment) this).getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }

    }

    public void getDistance(double lat1, double long1, double lat2, double long2){

        //calculate longitude diff
        double longDiff = long1 - long2;

        //calculate distance
        double distance = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(longDiff));

        distance = Math.acos(distance);

        //convert distance radian to degree
        distance = rad2deg(distance);

        //distance in miles
        distance = distance * 60 * 1.1515;

        //distance in kilo
        distance = distance * 1.609344;

        txtDistance = (TextView)getView().findViewById(R.id.txtDistance);

        txtDistance.setText("Distance : "+String.format(Locale.US,"%2f KM", distance));



    }

    private double rad2deg(double distance) {
        return (distance * 180.0 / Math.PI);
    }

    private double deg2rad(double lat1) {
        return (lat1 * Math.PI/180.0);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    public double getMekahLat(){
        return 21.422637698095706;
    }

    public double getMekahLong(){
        return 39.82619708312029;
    }

    public LatLng latMekah(){

        // Add a marker in Sydney and move the camera
        //3.206972843097494, 101.68101170482517

        //21.422637698095706, 39.82619708312029
        return new LatLng(21.422637698095706, 39.82619708312029);
        //mMap.addMarker(new MarkerOptions().position(mekah).title("Makkah"));

        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mekah,15));


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
}