package com.example.gek.teamwar;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.gek.teamwar.Data.Const;
import com.example.gek.teamwar.Data.Warior;
import com.example.gek.teamwar.Utils.Connection;
import com.example.gek.teamwar.Utils.FbHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;

public class LocationService extends Service
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private static final String TAG = "LOCATION_SERVICE";

    public LocationService() {
    }

    @Override
    public void onCreate() {
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

        Connection.getInstance().setServiceRunning(true);
        return START_STICKY;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initLocationSettings();
        Log.d(TAG, "onConnected: connect to GoogleApiClient");
    }

    /** Init configuration for location: priority, interval and callback*/
    private void initLocationSettings(){
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(Const.LOCATION_INTERVAL_UPDATE * 1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, mLocationListener);
        Log.d(TAG, "initLocationSettings: ");
    }


    /** Get coordinates of current WARIOR position */
    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                Log.d(TAG, "onConnected: Latitude = " + mLastLocation.getLatitude() +
                        " Longitude = " + mLastLocation.getLongitude());
                writePositionToDb(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            }

        }
    };

    private void writePositionToDb(Double latitude, Double longitude){
        Warior warior = new Warior();
        warior.setLatitude(latitude);
        warior.setLongitude(longitude);
        warior.setName(Connection.getInstance().getUserName());
        warior.setTeam(Connection.getInstance().getTeam());
        warior.setDate(new Date());
        FbHelper.updateWariorPosition(warior);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        Log.d(TAG, "onConnectionSuspended: ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: ");
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        Log.d(TAG, "onDestroy: disconnect from GoogleApiClient");
        super.onDestroy();
    }


}