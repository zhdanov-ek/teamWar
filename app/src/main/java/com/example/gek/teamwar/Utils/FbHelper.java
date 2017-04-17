package com.example.gek.teamwar.Utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



/**
 * Stored methods for work with FireBase database
 */

public class FbHelper {
    private static final String TAG = "FB_HELPER";
    public static final DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    private static final String CHILD_GROUP_NAME = "group_name";





}
