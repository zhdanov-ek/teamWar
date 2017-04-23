package com.example.gek.teamwar.Data;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by gek on 23.04.17.
 */

public class Mark {
    private String name;
    private String key;
    private String ownerName;
    private String ownerEmail;
    private double longitude;
    private double latitude;
    private Date date;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getOwnerName() {
        return ownerName;
    }
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }
    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
}
