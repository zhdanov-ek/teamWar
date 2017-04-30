package com.example.gek.teamwar;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.gek.teamwar.Data.Const;
import com.example.gek.teamwar.Data.Warior;
import com.example.gek.teamwar.Utils.Connection;
import com.example.gek.teamwar.Utils.FbHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class LocationService extends Service
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Handler handler = new Handler();
    private static final String TAG = "LOCATION_SERVICE";

    public LocationService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: init GoogleApiClient");
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
        Connection.getInstance(this).setServiceRunning(true);
        Log.d(TAG, "onStartCommand: setServiceRunning - true");
        return START_STICKY;
    }

    /** Init configuration for location: priority, interval and callback */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);

        handler.post(runnableGetLocation);
        Log.d(TAG, "onConnected: connect to GoogleApiClient");
    }

    // start every n-seconds for get location if service running
    private Runnable runnableGetLocation = new Runnable() {
        @Override
        public void run() {
            if (Connection.getInstance(getBaseContext()).getServiceRunning()){
                handler.postDelayed(this, Connection.getInstance(getBaseContext()).getFrequancyLocationUpdate()*1000);
                startLocationUpdates();
                handler.postDelayed(() -> stopLocationUpdates(), (Const.BASE_STEP_FREQUENCY - 1)*1000);
            } else {
                stopLocationUpdates();
            }
        }
    };


    /** set request for retrieve current location */
    private void startLocationUpdates() {
        if (mGoogleApiClient.isConnected()){
            if (checkLocationPermission()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            } else {
                Toast.makeText(getBaseContext(), "No permissions for location", Toast.LENGTH_SHORT).show();
            }
        } else {
            mGoogleApiClient.connect();
        }
    }


    /** stop listen update about our location */
    private void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    /** Get coordinates of current WARIOR position */
    @Override
    public void onLocationChanged(Location location) {
        if (location != null){
            Log.d(TAG, "onConnected: Latitude = " + location.getLatitude() +
                    " Longitude = " + location.getLongitude());
            Connection.getInstance(getBaseContext()).setLastLocation(
                    new LatLng(location.getLatitude(), location.getLongitude()));
            writePositionToDb(location.getLatitude(), location.getLongitude());
        }
    }

    private void writePositionToDb(Double latitude, Double longitude){
        Log.d(TAG, "writePositionToDb: ");
        Warior warior = new Warior();
        warior.setLatitude(latitude);
        warior.setLongitude(longitude);
        warior.setName(Connection.getInstance(this).getUserName());
        warior.setTeam(Connection.getInstance(this).getTeam());
        warior.setKey(Connection.getInstance(this).getUserKey());
        warior.setDate(new Date());
        FbHelper.updateWariorPosition(warior, this);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    private Boolean checkLocationPermission() {
        return ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                || (Build.VERSION.SDK_INT < Build.VERSION_CODES.M));
    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        Log.d(TAG, "onConnectionSuspended: ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        Log.d(TAG, "onDestroy: disconnect from GoogleApiClient");
        super.onDestroy();
    }

}