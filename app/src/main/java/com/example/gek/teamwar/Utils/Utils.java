package com.example.gek.teamwar.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gek.teamwar.R;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;


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



    /** define direction from point A to point B in human readable style */
    public static String getDirection(LatLng end, LatLng start){
        double radians = Math.atan2(
                (end.longitude - start.longitude),
                (end.latitude - start.latitude));
        double compassReading = radians * (180 / Math.PI);

        String[] directions = new String[] {"North", "NorthEast", "East", "SouthEast", "South",
                "SouthWest", "West", "NorthWest", "North"};
        int index = (int) Math.round(compassReading / 45);
        if (index < 0) {
            index = index + 8;
        }
        return directions[index];
    }

    /** Validate Latitude and Longitude */
    public static Boolean validateLatLong(double lat, double lng){
        return (((lat >= -90) && (lat <= 90)) &&
                ((lng >= -180) && (lng <= 180)));
    }

    /** Show custom Toast with marker info */
    public static void showToast(String distance, String direction, Date date, final Context ctx) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View layout = inflater.inflate(R.layout.layout_toast, null);

        TextView tvDirection = (TextView) layout.findViewById(R.id.tvDirection);
        TextView tvDistance = (TextView) layout.findViewById(R.id.tvDistance);
        TextView tvDateUpdate = (TextView) layout.findViewById(R.id.tvDateUpdate);

//        String dateUpdate = formatShort.format(new Date(new Date().getTime() - date.getTime()));

        tvDirection.setText(direction);
        tvDistance.setText(distance);
        tvDateUpdate.setText(formatDateUpdate(date));

        Toast toast = new Toast(ctx);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static String formatDateUpdate(Date date){
        String result;
        long oneHour = 60 * 60 * 1000;
        long oneDay = oneHour * 24;
        SimpleDateFormat formatFull = new SimpleDateFormat("yyyy.MM.dd, HH:mm");

        long delayTime = new Date().getTime() - date.getTime();
        if (delayTime < oneHour){
            result = secondsToHuman((int)delayTime/1000);
        } else if (delayTime > oneDay){
            result = formatFull.format(date);
        } else {
            result = secondsToHuman((int)delayTime/1000);
        }
        return result;
    }

    private static String secondsToHuman(int seconds){
        String result;
        if (seconds < 60) {
            result = seconds + " seconds";
        } else if ((60 * 60 > seconds) && (seconds > 59)){
            int min = seconds / 60;
            result = min + " min";
        } else {
            int hours = seconds / (60 * 60);
            int min = (seconds - (hours * 60 * 60))/60;
            result = hours + "h " + min + " min";
        }
        return result;
    }
}
