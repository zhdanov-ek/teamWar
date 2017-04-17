package com.example.gek.teamwar.Data;

import java.util.Date;

/**
 * Created by gek on 17.04.17.
 */

public class Warior {
    private String name;
    private String team;
    private int state;
    private double longitude;
    private double latitude;
    private Date date;


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }
    public void setTeam(String team) {
        this.team = team;
    }

    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
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
