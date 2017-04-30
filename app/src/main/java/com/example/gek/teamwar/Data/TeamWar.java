package com.example.gek.teamwar.Data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by gek on 30.04.17.
 */

public class TeamWar extends Application {
    private static TeamWar instance;

    public TeamWar() {
        super();
    }

    public static TeamWar getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

}
