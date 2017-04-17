package com.example.gek.teamwar;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.gek.teamwar.Data.Const;
import com.example.gek.teamwar.Data.Warior;
import com.example.gek.teamwar.Utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by gek on 17.04.17.
 */

public class MapActivity extends FragmentActivity
        implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MAP_ACTIVITY";
    private LinearLayout llContainer;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private float mZoomMap = Const.ZOOM_MAP;
    private SharedPreferences sharedPreferences;
    private CameraUpdate mCameraUpdate;
    private ArrayList<Warior> mListWariors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // restore saved zoom of camera
        sharedPreferences = getPreferences(MODE_PRIVATE);
        if (sharedPreferences.contains(Const.SETTINGS_ZOOM)) {
            mZoomMap = sharedPreferences.getFloat(Const.SETTINGS_ZOOM, Const.ZOOM_MAP);
        }

        llContainer = (LinearLayout) findViewById(R.id.llContainer);

        // get location courier from DB
       // Const.db.child(Const.CHILD_COURIER).addValueEventListener(mPositionWariorsListener);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

      //  bdPizza = BitmapDescriptorFactory.fromResource(R.drawable.local_pizza_map);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    // Map is ready. Check permissions and connect GoogleApiClient
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        connectToGoogleApiClient();
    }

    // GoogleApiClient is connected
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationAndMapSettings();
    }


    /**
     * Set listener for get location of client
     */
    private void locationAndMapSettings() {
        if (mGoogleApiClient.isConnected()) {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    || (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)) {

                // TODO: 17.04.17 Here need to start the service
//                mMap.setMyLocationEnabled(true);
//                LocationRequest locationRequest = LocationRequest.create()
//                        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
//                        .setInterval(Const.LOCATION_INTERVAL_UPDATE * 1000);
//                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
                updateUi();
            }
        } else {
            connectToGoogleApiClient();
        }
    }


//    /**
//     * Receive location of device - refresh the map
//     */
//    @Override
//    public void onLocationChanged(Location location) {
//        mClientLocation = new LatLng(location.getLatitude(), location.getLongitude());
//        if (mMap != null) {
//            updateUi();
//        }
//    }


    /**
     * Refresh the map
     */
    private void updateUi() {
        mMap.clear();
        if (mListWariors != null){
            for (Warior warior: mListWariors){
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(warior.getLongtitude(), warior.getLatitude()))
                        .title(warior.getName()));
            }
        }

    }


    /**
     * Listen location of courier from DB
     */
    private ValueEventListener mPositionWariorsListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            //Log.d(TAG, "onDataChange: get Location " + mPositionCourier.toString());
            if (mMap != null) {
                updateUi();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "onCancelled: " + databaseError.getDetails());
        }
    };


    private void connectToGoogleApiClient() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Const.REQUEST_CODE_LOCATION);
        } else {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Const.REQUEST_CODE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mGoogleApiClient.connect();
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            MapActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        showSnackToSettingsOpen();
                    }
                }
            }
        }
    }


    // If permission can enable from settings OS show SnackBar
    private void showSnackToSettingsOpen() {
        Snackbar.make(llContainer, R.string.permission_location_not_granded, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.openPermissionSettings(getBaseContext());
                    }
                })
                .show();
    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(Const.SETTINGS_ZOOM, mZoomMap).apply();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

}