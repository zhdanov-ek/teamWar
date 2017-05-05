package com.example.gek.teamwar.Data;

/**
 * Constants of the application
 */

public class Const {
    public static final String SETTINGS_ZOOM = "zoom_map";
    public static final String SETTINGS_NAME = "name";
    public static final String SETTINGS_PASS = "password";
    public static final String SETTINGS_EMAIL = "email";
    public static final String SETTINGS_FREQUANCY = "rate";
    public static final String SETTINGS_OLD_WARIORS = "old_wariors";

    public static final int REQUEST_CODE_LOCATION = 3;
    public static final float ZOOM_MAP = 10;

    public static final int BASE_STEP_FREQUENCY = 20;

    public static final int NOTIFICATION_ID = 1;

    public static final String EXTRA_LONGITUDE = "longitude";
    public static final String EXTRA_LATITUDE = "latitude";

    public static final String EXTRA_MODE = "mode_edit";
    public static final int MODE_MARK_NEW = 1;
    public static final int MODE_MARK_EDIT = 2;


    public static final int TYPE_MARK_MODE_CURRENT = 0;
    public static final int TYPE_MARK_MODE_MANUAL = 1;

    public static final int TYPE_MARK_NEUTRAL = 1;
    public static final int TYPE_MARK_OWN = 2;
    public static final int TYPE_MARK_ENEMY = 3;

    public static final double CRITICAL_TIME = 30 * 60 * 1000;
}
