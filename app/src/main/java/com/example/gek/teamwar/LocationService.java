package com.example.gek.teamwar;

import android.Manifest;
import android.app.PendingIntent;
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
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.gek.teamwar.Data.Const;
import com.example.gek.teamwar.Data.Warior;
import com.example.gek.teamwar.Utils.Connection;
import com.example.gek.teamwar.Utils.FbHelper;
import com.example.gek.teamwar.Utils.LogHelper;
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
    private LogHelper logHelper;

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
        Connection.getInstance().setServiceRunning(true);
        if (logHelper == null){
            logHelper = new LogHelper(getBaseContext());
        }

        Log.d(TAG, "onStartCommand: setServiceRunning - true");
        return START_STICKY;
    }

    /** Init configuration for location: priority, interval and callback */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setSmallestDisplacement(1f);

        handler.post(runnableGetLocation);
        showNotification();
        Log.d(TAG, "onConnected: connect to GoogleApiClient");
    }

    // start every n-seconds for get location if service running
    private Runnable runnableGetLocation = new Runnable() {
        @Override
        public void run() {
            if (Connection.getInstance().getServiceRunning()){
                logHelper.writeLog("start Runnable", new Date());
                handler.postDelayed(this, Connection.getInstance().getFrequancyLocationUpdate()*1000);
                startLocationUpdates();
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
            logHelper.writeLog("Remove location updates", new Date());
        }
    }

    /** Get coordinates of current WARIOR position */
    @Override
    public void onLocationChanged(Location location) {
        if (location != null){
            Log.d(TAG, "onConnected: Latitude = " + location.getLatitude() +
                    " Longitude = " + location.getLongitude());
            Connection.getInstance().setLastLocation(
                    new LatLng(location.getLatitude(), location.getLongitude()));
            writePositionToDb(location.getLatitude(), location.getLongitude());
            stopLocationUpdates();
        }
    }

    private void writePositionToDb(Double latitude, Double longitude){
        Log.d(TAG, "writePositionToDb: ");
        Warior warior = new Warior();
        warior.setLatitude(latitude);
        warior.setLongitude(longitude);
        warior.setName(Connection.getInstance().getUserName());
        warior.setTeam(Connection.getInstance().getTeam());
        warior.setKey(Connection.getInstance().getUserKey());
        warior.setDate(new Date());
        FbHelper.updateWariorPosition(warior);

        logHelper.writeLog(latitude + " - " + longitude + " (write to DB)", new Date());
    }

    private void showNotification() {
        NotificationCompat.Builder nfBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_man_run)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.mes_service_work))
                        .setShowWhen(false);

        // create pending intent used when tapping on the app notification
        // open up ScreenMapFragment
        Intent intent = new Intent(this, AuthActivity.class);


        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this, 0, intent, 0);
        nfBuilder.setContentIntent(pendingIntent);

        // end notification and mark service how high priority
        startForeground(Const.NOTIFICATION_ID, nfBuilder.build());
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