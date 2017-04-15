package com.example.gek.teamwar.Utils;

/**
 * Created by gek on 16.04.17.
 */

public class Connection {
    private static Connection instance;
    private String groupName;
    private String userName;
    private String userEmail;
    private int delayTransferLocation;

    public static synchronized Connection getInstance(){
        if (instance == null) {
            instance = new Connection();
        }
        return instance;
    }

    // Constructor
    private Connection(){
        groupName = "";
        userName = "";
        delayTransferLocation = 0;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
}
