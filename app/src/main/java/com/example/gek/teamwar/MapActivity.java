package com.example.gek.teamwar;

import android.Manifest;
import android.content.Intent;
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
import com.example.gek.teamwar.Utils.Connection;
import com.example.gek.teamwar.Utils.FbHelper;
import com.example.gek.teamwar.Utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    private BitmapDescriptor bdIam;
    private SharedPreferences sharedPreferences;
    private CameraUpdate mCameraUpdate;
    private ArrayList<Warior> mListWariors;
    private Boolean mIsAllReady = false;
    private LatLng mMyLocation;


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
        bdIam = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
//        bdPizza = BitmapDescriptorFactory.fromResource(R.drawable.local_pizza_map);

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
                mIsAllReady = true;
                updateUi();
                if (!Connection.getInstance().getServiceRunning()){
                    startService(new Intent(this,LocationService.class));
                }
            }
        } else {
            connectToGoogleApiClient();
        }
    }



    /**
     * Refresh the map
     */
    private void updateUi() {
        Log.d(TAG, "updateUi: ");
        mMap.clear();
        if (mListWariors != null){
            for (Warior warior: mListWariors) {
                // i am
                if (warior.getKey().contentEquals(Connection.getInstance().getUserKey())) {
                    mMyLocation = new LatLng(warior.getLatitude(), warior.getLongitude());
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(warior.getLatitude(), warior.getLongitude()))
                            .icon(bdIam)
                            .title("I am"));
                } else {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(warior.getLatitude(), warior.getLongitude()))
                            .title(warior.getName()));
                }
            }
        }

        // save current zoom of camera and set normal zoom if first show the map
        if (mMap.getCameraPosition().zoom > Const.ZOOM_MAP){
            mZoomMap = mMap.getCameraPosition().zoom;
        } else {
            if (mZoomMap < mMap.getCameraPosition().zoom){
                mZoomMap = Const.ZOOM_MAP;
            }
            if (mMyLocation != null){
                mCameraUpdate = CameraUpdateFactory.newLatLngZoom(mMyLocation, mZoomMap);
                mMap.moveCamera(mCameraUpdate);
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        FbHelper.db.child(Connection.getInstance().getGroupPassword())
                .child(FbHelper.CHILD_WARIORS)
                .addValueEventListener(mPositionWariorsListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FbHelper.db.child(Connection.getInstance().getGroupPassword())
                .child(FbHelper.CHILD_WARIORS)
                .removeEventListener(mPositionWariorsListener);
    }

    /**
     * Listen location of courier from DB
     */
    private ValueEventListener mPositionWariorsListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG, "onDataChange: get new position of wariors");
            if (mListWariors == null) {
                mListWariors = new ArrayList<>();
            }
            mListWariors.clear();
            for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                mListWariors.add(childSnapshot.getValue(Warior.class));
            }

            if ((mMap != null) && (mIsAllReady)) {
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
        editor.putFloat(Const.SETTINGS_ZOOM, mMap.getCameraPosition().zoom).apply();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

}