package com.example.gek.teamwar.Utils;

import android.content.Intent;
import android.content.SharedPreferences;

import com.example.gek.teamwar.Data.Const;
import com.example.gek.teamwar.Data.TeamWar;
import com.example.gek.teamwar.LocationService;
import com.google.android.gms.maps.model.LatLng;

/**
 * Store status of AUTH, state of service and value of basic parameters
 */

public class Connection {
    private static final int DEFAULT_FREQUANCY = 60;
    private static Connection instance;
    private String groupPassword;
    private String userName;
    private String userEmail;
    private String userKey;
    private String team;
    private LatLng lastLocation;
    private SharedPreferences sharedPreferences;
    private Boolean serviceRunning;
    private int frequancyLocationUpdate;
    private Boolean showOldWariors;

    public static synchronized Connection getInstance(){
        if (instance == null) {
            instance = new Connection();
        }
        return instance;
    }

    // Constructor
    private Connection(){
        sharedPreferences = TeamWar.getInstance().getPreferences();
        groupPassword = "";
        userName = "";
        userEmail = "";
        userKey = "";
        serviceRunning = false;
        showOldWariors = sharedPreferences.getBoolean(Const.SETTINGS_OLD_WARIORS, false);
        frequancyLocationUpdate = sharedPreferences.getInt(Const.SETTINGS_FREQUANCY, DEFAULT_FREQUANCY);
        userName = sharedPreferences.getString(Const.SETTINGS_NAME, "");
        groupPassword = sharedPreferences.getString(Const.SETTINGS_PASS, "");
        setUserEmail(sharedPreferences.getString(Const.SETTINGS_EMAIL, ""));
    }

    public void close(){
        groupPassword = "";
        userName = "";
        userEmail = "";
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

    public int getFrequancyLocationUpdate() {
        return frequancyLocationUpdate;
    }
    public void setFrequancyLocationUpdate(int frequancyLocationUpdate) {
        this.frequancyLocationUpdate = frequancyLocationUpdate;
        sharedPreferences.edit().putInt(Const.SETTINGS_FREQUANCY, frequancyLocationUpdate).apply();
    }

    public Boolean getShowOldWariors() {
        return showOldWariors;
    }
    public void setShowOldWariors(Boolean showOldWariors) {
        this.showOldWariors = showOldWariors;
        sharedPreferences.edit().putBoolean(Const.SETTINGS_OLD_WARIORS, showOldWariors).apply();
    }
}
