package com.example.gek.teamwar.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.gek.teamwar.Data.Const;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by gek on 16.04.17.
 */

public class Connection {
    public static final int DEFAULT_DELAY = 5*1000;
    private static Connection instance;
    private String groupPassword;
    private String userName;
    private String userEmail;
    private String userKey;
    private String team;
    private LatLng lastLocation;
    private int delayTransferLocation;
    private SharedPreferences sharedPreferences;
    private Boolean serviceRunning;

    public static synchronized Connection getInstance(Context ctx){
        if (instance == null) {
            instance = new Connection(ctx);
        }
        return instance;
    }

    // Constructor
    private Connection(Context ctx){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
        groupPassword = "";
        userName = "";
        userEmail = "";
        userKey = "";
        delayTransferLocation = DEFAULT_DELAY;
        serviceRunning = false;
        userName = sharedPreferences.getString(Const.SETTINGS_NAME, "");
        groupPassword = sharedPreferences.getString(Const.SETTINGS_PASS, "");
        setUserEmail(sharedPreferences.getString(Const.SETTINGS_EMAIL, ""));

    }

    public void close(){
        groupPassword = "";
        userName = "";
        userEmail = "";
        delayTransferLocation = DEFAULT_DELAY;
        sharedPreferences.edit().putString(Const.SETTINGS_NAME, "").apply();
        sharedPreferences.edit().putString(Const.SETTINGS_PASS, "").apply();
    }

    public String toString(){
        String s = "GroupPass=" + groupPassword +
                ", name=" + userName +
                ", email=" + userEmail +
                ", serviceRunning=" + serviceRunning;
        return s;
    }

    public String getGroupPassword() {
        return groupPassword;
    }
    public void setGroupPassword(String groupPassword) {
        this.groupPassword = groupPassword;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        userKey = Utils.removeCriticalSymbols(userEmail);
    }

    public int getDelayTransferLocation() {
        return delayTransferLocation;
    }
    public void setDelayTransferLocation(int delayTransferLocation) {
        this.delayTransferLocation = delayTransferLocation;
    }

    public Boolean getServiceRunning() {
        return serviceRunning;
    }
    public void setServiceRunning(Boolean serviceRunning) {
        this.serviceRunning = serviceRunning;
    }

    public String getTeam() {
        return team;
    }
    public void setTeam(String team) {
        this.team = team;
    }

    public String getUserKey() {
        return userKey;
    }

    public LatLng getLastLocation() {
        return lastLocation;
    }
    public void setLastLocation(LatLng lastLocation) {
        this.lastLocation = lastLocation;
    }
}
