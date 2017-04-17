package com.example.gek.teamwar.Utils;

/**
 * Created by gek on 16.04.17.
 */

public class Connection {
    public static final int DEFAULT_DELAY = 5*1000;
    private static Connection instance;
    private String groupPassword;
    private String userName;
    private String userEmail;
    private String team;
    private int delayTransferLocation;



    private Boolean serviceRunning;

    public static synchronized Connection getInstance(){
        if (instance == null) {
            instance = new Connection();
        }
        return instance;
    }

    // Constructor
    private Connection(){
        groupPassword = "";
        userName = "";
        userEmail = "";
        delayTransferLocation = DEFAULT_DELAY;
        serviceRunning = false;
    }

    public void close(){
        groupPassword = "";
        userName = "";
        userEmail = "";
        delayTransferLocation = DEFAULT_DELAY;
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
}
