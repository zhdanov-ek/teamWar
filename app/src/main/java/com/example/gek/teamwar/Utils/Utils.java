package com.example.gek.teamwar.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

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
    public static String createWariorKey(String email){
        email = email.replace(".", "");
        email = email.replace("$", "");
        email = email.replace("[", "");
        email = email.replace("]", "");
        email = email.replace("#", "");
        email = email.replace("/", "");
        return email;
    }
}
