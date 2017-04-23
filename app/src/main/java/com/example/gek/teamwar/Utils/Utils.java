package com.example.gek.teamwar.Utils;

import android.content.Context;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;


/**
 * Created by gek on 17.04.17.
 */

public class Utils {



    // Open system settings of program
    public static void openPermissionSettings(Context ctx) {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + ctx.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        ctx.startActivity(intent);
    }


    /** Make name KEY for FireBase.
     *  Delete character if found: . $ [ ] # /
     *  */
    public static String removeCriticalSymbols(String email){
        email = email.replace(".", "");
        email = email.replace("$", "");
        email = email.replace("[", "");
        email = email.replace("]", "");
        email = email.replace("#", "");
        email = email.replace("/", "");
        return email;
    }


    public static String getDistance(double lat1, double lon1, double lat2, double lon2) {
        int Radius = 6371;// radius of earth in Km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        Log.i("Radius Value",  " result   " + valueResult);

        int meter = (int)(valueResult * 1000);

        String result;
        if (meter > 999) {
            int km = meter / 1000;
            meter = meter % 1000;
            result = km + " km " + meter + " m";
        } else {
            result = meter + " m";
        }
        Log.i("Radius Value",  " distance = " + result);
        return result;
    }
}
