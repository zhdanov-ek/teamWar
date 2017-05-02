package com.example.gek.teamwar;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.gek.teamwar.Data.Const;
import com.example.gek.teamwar.Data.Mark;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;

/**
 * Show the map with wariors and objects
 *
 * https://developers.google.com/maps/documentation/android-api/marker?hl=ru
 */

public class MapActivity extends FragmentActivity
        implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private static final String TAG = "MAP_ACTIVITY";
    private RelativeLayout rlContainer;
    private FloatingActionButton fbAddObject;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private float mZoomMap = Const.ZOOM_MAP;
    private BitmapDescriptor bdIam;
    private SharedPreferences sharedPreferences;
    private CameraUpdate mCameraUpdate;
    private ArrayList<Warior> mListWariors;
    private ArrayList<Mark> mListMarks;
    private Boolean mIsAllReady = false;
    private LatLng mMyLocation;
    private IconGenerator mIconGenerator;
    private PolylineOptions mPathOptions;
    private Mark mChoosedMark = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // restore saved zoom of camera
        sharedPreferences = getPreferences(MODE_PRIVATE);
        if (sharedPreferences.contains(Const.SETTINGS_ZOOM)) {
            mZoomMap = sharedPreferences.getFloat(Const.SETTINGS_ZOOM, Const.ZOOM_MAP);
        }

        rlContainer = (RelativeLayout) findViewById(R.id.llContainer);
        fbAddObject = (FloatingActionButton) findViewById(R.id.fbAddObject);
        fbAddObject.setOnClickListener(v -> workWithMark());

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


        // Basis for objects
        mIconGenerator = new IconGenerator(this);
      //  mIconGenerator.setContentRotation(-90);
        mIconGenerator.setStyle(IconGenerator.STYLE_ORANGE);
    }

    // Map is ready. Check permissions and connect GoogleApiClient
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        connectToGoogleApiClient();
    }

    // GoogleApiClient is connected
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationAndMapSettings();
    }


    private void workWithMark(){
        if (mChoosedMark == null){
            if (mMyLocation != null) {
                Intent intentNewMark = new Intent(this, MarkActivity.class);
                intentNewMark.putExtra(Const.EXTRA_MODE, Const.MODE_MARK_NEW);
                intentNewMark.putExtra(Const.EXTRA_LATITUDE, mMyLocation.latitude);
                intentNewMark.putExtra(Const.EXTRA_LONGITUDE, mMyLocation.longitude);
                startActivity(intentNewMark);
            } else {
                Toast.makeText(this, "No location", Toast.LENGTH_SHORT).show();
            }
        } else {
            FbHelper.removeMark(mChoosedMark);
            Mark removedMark = mChoosedMark;
            mChoosedMark = null;
            fbAddObject.setImageResource(R.drawable.ic_add_location);
            Snackbar.make(fbAddObject, R.string.mes_mark_removed, Snackbar.LENGTH_LONG)
                    .setAction(R.string.restore, v -> {
                        FbHelper.updateMark(removedMark);
                        updateUi();
                    }).setActionTextColor(getResources().getColor(R.color.colorCyan))
                    .show();
            updateUi();
        }
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
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setRotateGesturesEnabled(true);
                mMap.getUiSettings().setMapToolbarEnabled(false);
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

        // get my location. Need for get distance to other markers. Execute only after onCreate
        if ((mMyLocation == null) && (mListWariors != null)) {
            for (Warior warior : mListWariors) {
                if (warior.getKey().contentEquals(Connection.getInstance().getUserKey())) {
                    mMyLocation = new LatLng(warior.getLatitude(), warior.getLongitude());
                    break;
                }
            }
        }
        mMap.clear();
        if (mListWariors != null){
            for (Warior warior: mListWariors) {
                String distance;
                // i am
                if (warior.getKey().contentEquals(Connection.getInstance().getUserKey())) {
                    mMyLocation = new LatLng(warior.getLatitude(), warior.getLongitude());
                    mMap.addMarker(new MarkerOptions()
                            .position(mMyLocation)
                            .icon(bdIam)
                            .title("I am")
                            .zIndex(1.0f));         // Show over other markers (other have index 0 (default)
                } else {
                    distance = " (" + Utils.getDistance(mMyLocation.latitude, mMyLocation.longitude,
                            warior.getLatitude(), warior.getLongitude()) + ")";
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(warior.getLatitude(), warior.getLongitude()))
                            .title(warior.getName() + distance));
                }
            }
        }

        if (mListMarks != null){
            for (Mark mark: mListMarks){
                String distance = "";
                if (mMyLocation != null){
                    distance = Utils.getDistance(mMyLocation.latitude, mMyLocation.longitude,
                            mark.getLatitude(), mark.getLongitude());
                }
                switch (mark.getType()){
                    case Const.TYPE_MARK_OWN:
                        mIconGenerator.setStyle(IconGenerator.STYLE_GREEN);
                        break;
                    case Const.TYPE_MARK_ENEMY:
                        mIconGenerator.setStyle(IconGenerator.STYLE_RED);
                        break;
                    case Const.TYPE_MARK_NEUTRAL:
                        mIconGenerator.setStyle(IconGenerator.STYLE_PURPLE);
                        break;
                    default:
                        mIconGenerator.setStyle(IconGenerator.STYLE_PURPLE);
                        break;
                }

                MarkerOptions markerOptions = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(mIconGenerator.makeIcon(mark.getName())))
                        .position(new LatLng(mark.getLatitude(), mark.getLongitude()))
                        .anchor(mIconGenerator.getAnchorU(), mIconGenerator.getAnchorV());
                mMap.addMarker(markerOptions.title(distance)).setTag(mark);
            }
        }

        if (mPathOptions != null){
            mMap.addPolyline(mPathOptions);
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

        FbHelper.db.child(Connection.getInstance().getGroupPassword())
                .child(FbHelper.CHILD_MARKS)
                .addValueEventListener(mLocationMarksListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FbHelper.db.child(Connection.getInstance().getGroupPassword())
                .child(FbHelper.CHILD_WARIORS)
                .removeEventListener(mPositionWariorsListener);

        FbHelper.db.child(Connection.getInstance().getGroupPassword())
                .child(FbHelper.CHILD_MARKS)
                .removeEventListener(mLocationMarksListener);
    }

    /**
     * Listen location of wariors from DB
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

    /**
     * Listen location of marks from DB
     */
    private ValueEventListener mLocationMarksListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG, "onDataChange: get list locations of marks");
            if (mListMarks == null) {
                mListMarks = new ArrayList<>();
            }
            mListMarks.clear();
            for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                mListMarks.add(childSnapshot.getValue(Mark.class));
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
        Snackbar.make(rlContainer, R.string.permission_location_not_granded, Snackbar.LENGTH_LONG)
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

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (marker.getPosition() != mMyLocation){
            mPathOptions = new PolylineOptions()
                    .color(getResources().getColor(R.color.colorRed));
            mPathOptions.add(mMyLocation);
            mPathOptions.add(marker.getPosition());

            String direction = Utils.getDirection(marker.getPosition(), mMyLocation);
            Toast.makeText(this, "Direction  " + direction, Toast.LENGTH_LONG).show();
            updateUi();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTag() != null){
            Mark choosedMark = (Mark) marker.getTag();
            fbAddObject.setImageResource(R.drawable.ic_delete);
            mChoosedMark = choosedMark;
        } else {
            fbAddObject.setImageResource(R.drawable.ic_add_location);
            mChoosedMark = null;
        }
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mChoosedMark != null){
            fbAddObject.setImageResource(R.drawable.ic_add_location);
            mChoosedMark = null;
            Toast.makeText(this, "reset mark ", Toast.LENGTH_SHORT).show();
        }
    }
}